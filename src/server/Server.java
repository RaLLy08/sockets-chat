package src.server;
import java.net.*;
import java.util.*;

import src.dto.MessageDto;
import src.server.services.RoomService;

import java.io.*;



public class Server extends Thread {
   private Set<ClientHandler> clientHandlers = new HashSet<>();
   public static void main(String [] args) {
      if (args.length < 1) {
         System.out.println("Syntax: java Server <port>");
         System.exit(0);
     }

      int port = Integer.parseInt(args[0]);

      new Server(port);
   }

   Server(int port) {
      try (ServerSocket serverSocket = new ServerSocket(port)) {
         System.out.println(
            "Server started" + 
            serverSocket.getLocalPort()
         );

         while(true) {
            Socket socket = serverSocket.accept();
               
            System.out.println("Client connected: " + socket.getRemoteSocketAddress());
           
            RoomService roomService = new RoomService();

            Thread clientHandler = new ClientHandler(
               socket,
               roomService
            );
   
            this.clientHandlers.add((ClientHandler) clientHandler);
            
            clientHandler.start();
         }

      } catch (IOException e) {
         System.out.println("Error in the server: " + e.getMessage());
         e.printStackTrace();
      }
   }
}


class ClientHandler extends Thread {
   private Socket socket;
   private ObjectInputStream in;
   private DataOutputStream out;
   private RoomService roomService;

   ClientHandler(Socket socket, RoomService roomService) throws IOException {
      this.socket = socket;
      this.roomService = roomService;

      InputStream socketInputStream = socket.getInputStream();
      this.in = new ObjectInputStream(
         socketInputStream
      );
      
      OutputStream socketOutputStream = socket.getOutputStream();
      this.out = new DataOutputStream(
         socketOutputStream
      );
   }

   public void run() {
      System.out.println("Connected to: " + this.socket.getRemoteSocketAddress());

      while (true) {

         try {
            MessageDto messageDto = (MessageDto) this.in.readObject();

            // String message = this.in.readUTF();
            System.out.println(messageDto);


         } catch (IOException e) {
            this.close();
            e.printStackTrace();
            break;
         } catch (ClassNotFoundException e) {
            e.printStackTrace();
         }
      }

   }

   public void close() {
      try {
         System.out.println("Client disconnected. " + this.socket.getRemoteSocketAddress());

         this.in.close();
         this.out.close();
         this.socket.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}