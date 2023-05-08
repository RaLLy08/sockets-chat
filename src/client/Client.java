package src.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

import src.client.ChatCommandHandler.Command;
import src.client.ConsoleReader.ConsoleSubsriber;
import src.shared.ChatCommands;
import src.shared.MessageDto;



public class Client extends ConsoleSubsriber {
   Socket socket;
   String room;
   ObjectOutputStream out;

   Client(String host, int port, String room) throws IOException {
      this.socket = new Socket(
         host,
         port
      );
      System.out.println("Connected to: " + socket.getRemoteSocketAddress());

      out = new ObjectOutputStream(
         socket.getOutputStream()
      );

      System.out.println(
         String.format(
            "Enter text to send message to the lobby\nWrite '%s <room>' to join or create the room.\n", ChatCommands.JOIN
         )
      );

      this.room = room;
   }

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

   public void onInputText(String text) {
      try {
         this.out.writeObject(
            new MessageDto(
               this.room,
               text,
               MessageDto.Action.MESSAGE
            )  
         );
      } catch (IOException e) {
         e.printStackTrace();
      }
   }


   public void onInputCommand(Command chatCommand, String param) {
      try {

         if (chatCommand == Command.JOIN) {
            this.out.writeObject(
               new MessageDto(
                  param, // room
                  MessageDto.Action.JOIN
               )
            );
         } 
         
         if (chatCommand == Command.QUIT) {
            this.out.writeObject(
               new MessageDto(
                  this.room,
                  MessageDto.Action.LEAVE
               )
            );
         } 

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

   public static void main(String [] args) {
      try {
         String serverHost = args[0];
         int serverPort = Integer.parseInt(args[1]);
         String roomName = null;


         Client client = new Client(
            serverHost, 
            serverPort,
            roomName
         );

         Thread messageConsumerThread = new Thread(() -> client.consumeMessages());
         Thread consoleReader = new ConsoleReader(client);

         messageConsumerThread.start();
         consoleReader.start();

      }  catch (IOException e) {
         e.printStackTrace();
      }
   }
}