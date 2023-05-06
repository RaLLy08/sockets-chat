package src.server;

import java.net.*;
import java.util.*;
import java.io.*;

import src.dto.MessageDto;
import src.server.service.RoomService;


class ClientController {
   private Set<ClientHandler> clientHandlers = new HashSet<>();

   ClientController() {

   }


   public void addClientHandler(ClientHandler clientHandler) {
      this.clientHandlers.add(clientHandler);
   }

}

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


class ClientHandler extends Thread {
   static Set<ClientHandler> clientHandlers = new HashSet<>();
   private Socket socket;
   private ObjectInputStream in;
   private ObjectOutputStream out;
   private String room;

   ClientHandler(
      Socket socket
   ) throws IOException {
      this.socket = socket;
      InputStream socketInputStream = socket.getInputStream();
      this.in = new ObjectInputStream(
         socketInputStream
      );
      
      OutputStream socketOutputStream = socket.getOutputStream();
      this.out = new ObjectOutputStream(
         socketOutputStream
      );
   }

   void broadcast(String message) {
      System.out.println("Broadcasting message: " + clientHandlers.size());

      for (ClientHandler clientHandler : clientHandlers) {
         if (clientHandler != this) {
            clientHandler.sendMessage(message);
         }
      }
   }

   void sendMessage(String text) {
      try {
         System.out.println("Sending message to client: " + text);
         MessageDto messageDto = new MessageDto(null, text);

         this.out.writeObject(messageDto);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   void setRoom(String room) {
      this.room = room;
   }

   boolean checkRoom(String room) {
      return this.room == room;
   }

   @Override
   public void run() {
      System.out.println("Connected to: " + this.socket.getRemoteSocketAddress());

      clientHandlers.add((ClientHandler) this);

      while (true) {

         try {
            MessageDto messageDto = (MessageDto) this.in.readObject();

            this.setRoom(messageDto.room);

            System.out.println(
               messageDto.text
            );

            this.broadcast(messageDto.text);

         } catch (IOException | ClassNotFoundException e) {
            this.close();
            break;
         }
      }

   }

   public void close() {
      try {
         System.out.println("Client disconnected. " + this.socket.getRemoteSocketAddress());

         clientHandlers.remove(this);

         this.in.close();
         this.out.close();
         this.socket.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}