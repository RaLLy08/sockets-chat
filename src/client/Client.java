package src.client;

import java.net.*;

import src.dto.MessageDto;

import java.io.*;


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

            System.out.println(
               messageDto.text
            );
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

         while (true) {
            System.out.print("Enter text: ");
            String text = br.readLine();

            out.writeObject(
               new MessageDto(this.room, text)
            );
         }

         // socket.close();
      } catch (IOException e) {
         e.printStackTrace();
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


         BufferedReader br = new BufferedReader(
            new InputStreamReader(System.in)
         );

         System.out.print("Write room name: ");
         String roomName = br.readLine();

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