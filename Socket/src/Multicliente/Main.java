/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Multicliente;
 
import java.io.IOException;
import java.net.ServerSocket;
 
public class Main {
 
    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;
 
        try {
            serverSocket = new ServerSocket(5000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 5000.");
            System.exit(-1);
        }
 
        while (listening) { 
            new MultiClientServer(serverSocket.accept()).start(); }
 
        serverSocket.close();
    }
 
 
}