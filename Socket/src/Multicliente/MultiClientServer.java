/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Multicliente;

/**
 *
 * @author Jhon
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
 
public class MultiClientServer extends Thread {
    private Socket socket = null;
 
    public MultiClientServer(Socket socket) {
        super("MultiClientServer");
        this.socket = socket;
    }
 
    public void run() {
 
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
     
            String inputLine;
     
             
            while ((inputLine = in.readLine()) != null) {
             
                System.out.println(inputLine);
             
            }
            out.close();
            in.close();
            socket.close();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
 
}