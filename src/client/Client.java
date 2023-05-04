package src.client;
import java.net.*;

import src.dto.MessageDto;

import java.io.*;


public class Client {
   Socket socket;
   BufferedReader br;  
   MessageDto messageDto;

   public void consumeMessages() {
      System.out.println("MessageConsumerThread started");

      // try {
      //    InputStream inFromServer = this.socket.getInputStream();
      //    ObjectInputStream in = new ObjectInputStream(inFromServer);
      
      //    while (true) {
      //       System.out.println(
      //          in.read()
      //       );
      //    }

      // } catch (IOException e) {
      //    e.printStackTrace();
      // }

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

            this.messageDto.text = text;

            out.writeObject(this.messageDto);
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
      this.messageDto = new MessageDto(room);
   }


   public static void main(String [] args) {
      try {
         String serverHost = args[0];
         int serverPort = Integer.parseInt(args[1]);


         BufferedReader br = new BufferedReader(
            new InputStreamReader(System.in)
         );

         System.out.print("Write room name");
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