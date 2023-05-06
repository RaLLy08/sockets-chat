package src.server;

import java.net.*;
import java.util.*;
import java.io.*;

import src.dto.MessageDto;

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
   
   private class ClientHandler extends Thread {
      static Map<
         String, 
         HashSet<ClientHandler>
      > clientHandlersRooms = new HashMap<>();
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

      private HashSet<ClientHandler> getClientHandlers() {
         if (this.room == null) {
            throw new RuntimeException("Room is not set");
         }

         return ClientHandler.clientHandlersRooms.get(this.room);
      }

      void broadcast(String message) {
         HashSet<ClientHandler> roomClientHandlers = this.getClientHandlers();

         for (ClientHandler clientHandler : roomClientHandlers) {
            if (clientHandler != this) {
               clientHandler.sendMessage(message);
            }
         }
      }

      void addClientHandlerToRoom() {
         HashSet<ClientHandler> roomClientHandlers = this.getClientHandlers();

         if (roomClientHandlers == null) {
            System.out.println("New room has been created: " + room);

            roomClientHandlers = new HashSet<>();
         } else {
            String message = String.format(
               "New Client Jointed to %s room\nNumber of users in room: %d", 
               room, roomClientHandlers.size()
            );

            System.out.println(message);
         }

         roomClientHandlers.add(this);

         clientHandlersRooms.put(this.room, roomClientHandlers);
      }

      void removeClientHandlerFromRoom() {
         HashSet<ClientHandler> roomClientHandlers = this.getClientHandlers();

         if (roomClientHandlers == null) {
            return;
         }

         roomClientHandlers.remove(this);

         clientHandlersRooms.put(this.room, roomClientHandlers);
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

      @Override
      public void run() {
         System.out.println("Connected to: " + this.socket.getRemoteSocketAddress());

         while (true) {
            try {
               MessageDto messageDto = (MessageDto) this.in.readObject();

               if (this.room == null) {
                  this.setRoom(messageDto.room);
                  this.addClientHandlerToRoom();
               }

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

            this.removeClientHandlerFromRoom();

            this.in.close();
            this.out.close();
            this.socket.close();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
}

