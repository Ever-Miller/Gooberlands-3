/**
 * File: BattleClient.java
 * Purpose:
 *      Handles the client-side networking logic for a multiplayer battle.
 *
 *      This class establishes a socket connection to a local battle server
 *      and provides simple send/receive methods for exchanging battle data
 *      (such as damage values) between client and server.
 *
 *      This implementation uses blocking I/O and is intended for
 *      straightforward turn-based communication rather than real-time play.
 */

package network;

import java.io.*;
import java.net.*;

/**
 * Client-side networking component for battle communication.
 */
public class BattleClient{

    private int portNum = 50000;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    /**
     * Constructs a new BattleClient and immediately attempts
     * to connect to the server.
     */
    public BattleClient(){
        connection();
    }

    /**
     * Establishes a socket connection to the battle server
     * running on the local host.
     */
    public void connection(){
        try{
            // Connect to server
            socket = new Socket("localhost", portNum);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Receives data from the server.
     *
     * @return the integer value received from the server
     *         (typically representing HP loss or damage dealt)
     */
    public int getFromServer(){
        try{
            int hpLoss = Integer.parseInt(in.readLine());
            return hpLoss;
        }catch(IOException e){
            e.printStackTrace();
            return 0;
        }

    }

    /**
     * Sends an integer value to the server.
     *
     * @param damageAmount the value to transmit
     *                     (typically damage dealt this turn)
     */
    public void sendToServer(int damageAmount){
        out.println(damageAmount);
    }
}
