/**
 * File: NetworkDemo.java
 * Purpose:
 *      Demonstrates basic client-server communication for the
 *      multiplayer battle networking system.
 *
 *      This class launches a {@link BattleServer} in a background thread,
 *      then creates a {@link BattleClient} to simulate a simple turn-based
 *      exchange of damage values.
 *
 *      Intended for testing and demonstration purposes only.
 */

package network;

import java.io.*;
import java.net.*;

/**
 * Simple demonstration driver for the battle networking system.
 */
public class NetworkDemo{

    /**
     * Entry point for the network demo.
     * <p>
     * Starts a server instance on a separate thread, waits briefly
     * for it to initialize, then simulates a client sending and
     * receiving damage data.
     * </p>
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args){
        new Thread(()->{
            new BattleServer();
        }).start();
        
        try{
            Thread.sleep(200);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        BattleClient client = new BattleClient();

        System.out.println("My pokemon attacked to opponent pokemon by 90 damage");
        client.sendToServer(90);

        // Imagin its opponent
        int damage = client.getFromServer();
        System.out.println("My pokemon got " + damage + " damage");
    }
}
