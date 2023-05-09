package src.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
   public static void main(String [] args) {
      int port = Integer.parseInt(args[0]);

      new Server(port);
   }
   Server(int port) {
      try (ServerSocket serverSocket = new ServerSocket(port)) {
         System.out.println(
            "Server started on port: " + 
            serverSocket.getLocalPort()
         );

         while(true) {
            Socket socket = serverSocket.accept();
               
            System.out.println("Client connected: " + socket.getRemoteSocketAddress());
           
            Thread clientHandler = new ClientHandler(
               socket
            );
   
            clientHandler.start();
         }

      } catch (IOException e) {
         System.out.println("Error in the server: " + e.getMessage());
         e.printStackTrace();
      }
   }
}

