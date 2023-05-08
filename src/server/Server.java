package src.server;

import java.net.*;
import java.util.*;
import java.io.*;

import src.shared.MessageDto;



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
      /*
       * This is a map of rooms and the client handlers that are in that room.
       */
      static Map<
         String, 
         HashSet<ClientHandler>
      > clientHandlersRooms = new HashMap<>();
      /*
       * This is a set of client handlers that are in the lobby.
       */
      static HashSet<ClientHandler> clientHandlersLobby = new HashSet<>();
      
      private Socket socket;
      private ObjectInputStream in;
      private ObjectOutputStream out;
      private String room;
      private String remoteSocketAddress;

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

      private HashSet<ClientHandler> getRoomClientHandlers() {
         return ClientHandler.clientHandlersRooms.get(this.room);
      }

      void broadcast(MessageDto messageDto) {
         HashSet<ClientHandler> clientHandlers;

         if (this.room == null) {
            clientHandlers = ClientHandler.clientHandlersLobby;
         } else {
            clientHandlers = this.getRoomClientHandlers();
         }

         for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler != this) {
               clientHandler.sendMessage(messageDto);
            } else {
               System.out.println(
                  new MessageDto(this.room, messageDto.text, this.remoteSocketAddress, messageDto.action)
               );
            }
         }
      }

      void addClientHandlerToRoom() {
         HashSet<ClientHandler> roomClientHandlers = this.getRoomClientHandlers();

         if (roomClientHandlers == null) {
            System.out.println("New room has been created: " + this.room);
            this.sendMessage(
               ResponseMessage.newRoomCreated(this.room)
            );

            roomClientHandlers = new HashSet<>();
         } else {

            this.sendMessage(
               ResponseMessage.joinedRoom(this.room)
            );
            this.broadcast(
               ResponseMessage.broadcastJoinRoom(
                  this.remoteSocketAddress,
                  roomClientHandlers.size() + 1
               )
            );
         }

         roomClientHandlers.add(this);
         clientHandlersRooms.put(this.room, roomClientHandlers);
      }

      void addToLobby() {
         if (ClientHandler.clientHandlersLobby.contains(this)) {
            this.sendMessage(
               ResponseMessage.clientAlreadyInLobby().addRoom(this.room)
            );
            return;
         }

         ClientHandler.clientHandlersLobby.add(this);
      }

      void removeFromRoom() {
         HashSet<ClientHandler> roomClientHandlers = this.getRoomClientHandlers();

         if (roomClientHandlers == null) {
            return;
         }

         roomClientHandlers.remove(this);
         clientHandlersRooms.put(this.room, roomClientHandlers);

         this.sendMessage(
            ResponseMessage.leftRoom()
         );
         this.broadcast(
            ResponseMessage.broadcastLeaveRoom(
               this.remoteSocketAddress,
               roomClientHandlers.size()
            )
         );
      }

      void removeFromLobby() {
         ClientHandler.clientHandlersLobby.remove(this);
      }

      void sendMessage(MessageDto messageDto) {
         try {
            System.out.println(
               messageDto
            );
            // overriding the room and remoteSocketAddress
            messageDto.room = this.room;
            messageDto.remoteSocketAddress = this.remoteSocketAddress;

            if (messageDto.action == MessageDto.Action.MESSAGE) {
               messageDto.text = String.format(
                  "%s: %s", 
                  this.remoteSocketAddress, 
                  messageDto.text
               );
            }

            this.out.writeObject(messageDto);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      void setRoom(String room) {
         this.room = room;
      }

      void assignToRoom(String room) {
         if (room == null) {
            this.sendMessage(
               ResponseMessage.roomNotProvided()
            );
            return;
         }

         if (this.room != null) {
            this.sendMessage(
               ResponseMessage.roomAlreadySet()
            );
            return;
         }

         this.setRoom(room);
         this.addClientHandlerToRoom();
      }

      void revokeRoom() {
         if (this.room == null) {
            this.sendMessage(
               ResponseMessage.notInRoom()
            );
            
            return;
         }

         this.removeFromRoom();
         this.setRoom(null);
      }

      private void handleMessage(MessageDto messageDto) {
         this.remoteSocketAddress = this.socket.getRemoteSocketAddress().toString();

         if (messageDto.action == MessageDto.Action.JOIN) {
            this.removeFromLobby();
            this.assignToRoom(messageDto.room);
         } else if (messageDto.action == MessageDto.Action.LEAVE) {
            this.addToLobby();
            this.revokeRoom();
         } else if (messageDto.action == MessageDto.Action.MESSAGE) {
            this.broadcast(messageDto);
         } else if (messageDto.action == MessageDto.Action.GET_ROOM_LIST) {
            this.sendMessage(
               ResponseMessage.listRooms(
                  ClientHandler.clientHandlersRooms.keySet().toString()
               )
            );
         } else if (messageDto.action == MessageDto.Action.GET_ROOM_MEMBERS) {
            HashSet<ClientHandler> roomClientHandlers = this.getRoomClientHandlers();

            if (roomClientHandlers == null) {
               this.sendMessage(
                  ResponseMessage.roomNotProvided()
               );
               return;
            }

            this.sendMessage(
               ResponseMessage.listRoomMembers(
                  Arrays.toString(roomClientHandlers.stream()
                  .map(ClientHandler::remoteSocketAddress)
                  .toArray(String[]::new))
               )
            );
         }
         
         else {
            this.sendMessage(
               ResponseMessage.invalidAction()
            );
         }

      }


      String remoteSocketAddress() {
         return this.remoteSocketAddress;
      }

      @Override
      public void run() {
         System.out.println("Connected to: " + this.socket.getRemoteSocketAddress());

         this.addToLobby();

         while (true) {
            try {
               MessageDto messageDto = (MessageDto) this.in.readObject();
          
               this.handleMessage(messageDto);
            } catch (IOException | ClassNotFoundException e) {
               this.close();
               break;
            }
         }

      }

      public void close() {
         try {
            System.out.println(String.format("Client %s disconnected.", this.socket.getRemoteSocketAddress()));

            if (this.room != null) {
               this.removeFromRoom();
            } else {
               this.removeFromLobby();
            }

            this.in.close();
            this.out.close();
            this.socket.close();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
}

