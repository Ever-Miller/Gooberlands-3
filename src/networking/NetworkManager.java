/**
 * File: NetworkManager.java
 * Purpose:
 * 		Provides a higher-level networking abstraction for multiplayer gameplay.
 * 	
 * 		This class handles hosting and client connections, sending and receiving
 *		serialized objects over a socket connection, and dispatching incoming data
 *		to a caller-provided listener.
 *
 *		It is designed to support turn-based multiplayer communication.
 */

package networking;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Manages client–server communication for multiplayer sessions.
 *
 * <p>Typical usage:</p>
 * <pre>
 * 		NetworkManager net = new NetworkManager();
 * 		net.setListener(obj -> handleIncoming(obj));
 * 
 * 		// Host:
 *		net.startHost(4000);
 *   
 *		// or Client:
 *		net.connectTo("XXX.X.X.X", 4000);
 * </pre>
 *
 * <p>		The listener is invoked on a dedicated network thread;
 *		callers should use {@code Platform.runLater(...)} when updating JavaFX UI.</p>
 */
public class NetworkManager {
	
	/** The active socket connection. */
	private Socket socket;
	
	/** Stream used to send serialized objects to the remote peer. */
	private ObjectOutputStream out;

	/** Stream used to receive serialized objects from the remote peer. */
	private ObjectInputStream in;

	/** Controls the message listening loop. */
	private volatile boolean running = false;

	/** Callback invoked whenever data is received from the network. */
	private Consumer<Object> listener;

	/**
	 * Creates a new NetworkManager with no active connection.
	 *
	 * <p> A listener may be installed via {@link #setListener(Consumer)}
	 * before or after {@link #startHost(int)} / {@link #connectTo(String, int)}.</p>
	 */
	public NetworkManager() {
		//no-op
	}

	/**
	 * Sets or replaces the listener used to handle incoming messages.
	 *
	 * @param listener a {@link Consumer} that will be invoked for each object
	 * received from the remote peer; may be {@code null} to clear
	 */
	public synchronized void setListener(Consumer<Object> listener) {
		this.listener = listener;
	}

	/**
	 * Starts listening as a host on the given port and blocks until a client connects.
	 *
	 * <p>This method should be called from a background thread, as it will block
	 * on {@link ServerSocket#accept()} until a client connects.</p>
	 *
	 * @param port TCP port to listen on
	 * @throws IOException if an I/O error occurs while opening or accepting the socket
	 */
	public void startHost(int port) throws IOException {
		ServerSocket server = new ServerSocket(port);
		System.out.println("[HOST] Waiting for connection on port " + port + "...");
		socket = server.accept();
		server.close(); // single-use server

		System.out.println("[HOST] Connection established!");
		initializeStreams();
	}

	/**
	 * Connects to a remote host on the given address and port.
	 *
	 * <p>This method should also be called from a background thread,
	 * as the socket connection may block briefly.</p>
	 *
	 * @param host hostname or IP address of the host
	 * @param port TCP port to connect to
	 * @throws IOException if an I/O error occurs while connecting
	 */
	public void connectTo(String host, int port) throws IOException {
		System.out.println("[CLIENT] Connecting to " + host + ":" + port + "...");
		socket = new Socket(host, port);
		System.out.println("[CLIENT] Connected!");
		initializeStreams();
	}

	/**
	 * Initializes the object streams and starts the background reader thread.
	 *
	 * @throws IOException if stream creation fails
	 */
	private void initializeStreams() throws IOException {
		out = new ObjectOutputStream(socket.getOutputStream());
		in  = new ObjectInputStream(socket.getInputStream());
		running = true;
		startReaderThread();
	}

	/**
	 * Spawns a dedicated daemon thread that continuously reads objects
	 * from the input stream and forwards them to the listener.
	 */
	private void startReaderThread() {
		Thread t = new Thread(() -> {
			try {
				while (running) {
					Object obj = in.readObject();  // blocks
					Consumer<Object> currentListener;
					synchronized (this) {
						currentListener = this.listener;
					}
					if (currentListener != null && obj != null) {
						currentListener.accept(obj);
					}
				}
			} catch (EOFException eof) {
				System.out.println("[NET] Remote closed connection.");
			} catch (Exception errpr) {
				// Only log if we weren't intentionally shutting down
				if (running) {
					System.err.println("[NET] Error in reader thread:");
					errpr.printStackTrace();
				}
			} finally {
				close();
			}
		}, "Network-Reader");
		t.setDaemon(true);
		t.start();
	}

	/**
	 * Sends a serialized object to the remote peer.
	 *
	 * @param obj object to send; must be {@link java.io.Serializable}
	 */
	public synchronized void send(Object obj) {
		if (out == null) {
			System.err.println("[NET] Attempted to send on a closed or uninitialized connection.");
			return;
		}
		try {
			out.writeObject(obj);
			out.flush();
		} catch (IOException error) {
			System.err.println("[NET] Failed to send object:");
			error.printStackTrace();
		}
	}

	/**
	 * Closes the network connection and stops the reader thread.
	 *
	 * <p>This method is idempotent; calling it multiple times is safe.</p>
	 */
	public void close() {
		running = false;
		try {
			if (in != null) in.close();
		} catch (IOException ignored) {}

		try {
			if (out != null) out.close();
		} catch (IOException ignored) {}

		try {
			if (socket != null) socket.close();
		} catch (IOException ignored) {}
		
		in = null;
		out = null;
		socket = null;
	}
	

}
