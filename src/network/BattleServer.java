/**
 * File: BattleServer.java
 * Purpose:
 *      Handles server-side networking logic for a multiplayer battle.
 *
 *      This class listens on a predefined port, accepts a single client
 *      connection, and facilitates basic turn-based data exchange between
 *      the server and the connected client.
 *
 *      This implementation is intended for simple, local multiplayer
 *      testing and uses blocking I/O.
 */

package network;

import java.io.*;
import java.net.*;

/**
 * Server-side networking component for battle communication.
 */
public class BattleServer{

    private int portNum = 50000;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    /**
     * Constructs a new BattleServer and immediately waits for
     * a client connection.
     */
    public BattleServer(){
        connection();
    }

    /**
     * Initializes the server socket, waits for a client to connect,
     * and sets up input/output streams for communication.
     */
    public void connection(){
        try{
            serverSocket = new ServerSocket(portNum);
            // wait at portNum
            clientSocket = serverSocket.accept();
            // complete connection

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            
            out.println(in.readLine());
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
