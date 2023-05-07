package src.client;

import java.net.*;

import src.dto.MessageDto;

import java.io.*;

class ChatCommands {
   static final String JOIN = "/join";
   static final String QUIT = "/quit";
}

public class Client {
   Socket socket;
   BufferedReader br;  
   String room;

   public void consumeMessages() {
      System.out.println("MessageConsumerThread started");

      try {
         InputStream inFromServer = this.socket.getInputStream();
         ObjectInputStream in = new ObjectInputStream(inFromServer);
      
         while (true) {
            MessageDto messageDto = (MessageDto) in.readObject();

            this.handleMessage(messageDto);
         }

      } catch (IOException e) {
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }

   }

   public void sendMessages() {
      try {
         System.out.println("Connected to: " + socket.getRemoteSocketAddress());

         OutputStream outputStream = socket.getOutputStream();
         ObjectOutputStream out = new ObjectOutputStream(
            outputStream
         );

         br = new BufferedReader(
            new InputStreamReader(System.in)
         );
         System.out.print("Enter text to send message to the lobby\nWrite '/connect <room>' to connect or create the room.\n");

         while (true) {
            String text = br.readLine();

            if (text == null) continue;

            MessageDto messageDto;

            String[] parts = text.split("\\s+");

            if (parts[0].equals("/connect")) {
               this.room = parts[1];

               messageDto = new MessageDto(
                  this.room,
                  MessageDto.Action.JOIN
               );
            } else if (parts[0].equals("/leave")) {
               messageDto = new MessageDto(
                  this.room,
                  MessageDto.Action.LEAVE
               );
            } else {
               messageDto = new MessageDto(
                  this.room,
                  text,
                  MessageDto.Action.MESSAGE
               );
            }

            out.writeObject(
               messageDto
            );
         }

         // socket.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   void handleMessage(MessageDto messageDto) {

      if (messageDto.action == MessageDto.Action.ERROR) {
         System.out.println(
            "Error: " + messageDto.text
         );
      } else {
         System.out.println(
            messageDto.text
         );
      }

   }


   Client(String host, int port, String room) throws IOException {
      this.socket = new Socket(
         host,
         port
      );
      this.room = room;
   }


   public static void main(String [] args) {
      try {
         String serverHost = args[0];
         int serverPort = Integer.parseInt(args[1]);
         String roomName = null;

         // BufferedReader br = new BufferedReader(
         //    new InputStreamReader(System.in)
         // );

         // System.out.print("Write room name: ");
         // String roomName = br.readLine();

         Client client = new Client(
            serverHost, 
            serverPort,
            roomName
         );

         Thread messageConsumerThread = new Thread(() -> client.consumeMessages());
         Thread messageSenderThread = new Thread(() -> client.sendMessages());

         messageConsumerThread.start();
         messageSenderThread.start();
      }  catch (IOException e) {
         e.printStackTrace();
      }
   }
}