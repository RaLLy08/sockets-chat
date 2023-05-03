import java.net.*;
import java.io.*;


public class Client {
   Socket socket;
   BufferedReader br;  

   public void consumeMessages() {
      System.out.println("MessageConsumerThread started");

      try {
         InputStream inFromServer = this.socket.getInputStream();
         DataInputStream in = new DataInputStream(inFromServer);
      
         while (true) {
            System.out.println(
               in.readUTF()
            );
         }

      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   public void sendMessages() {
      try {
         System.out.println("Connected to: " + socket.getRemoteSocketAddress());

         DataOutputStream out = new DataOutputStream(
            socket.getOutputStream()
         );

         br = new BufferedReader(
            new InputStreamReader(System.in)
         );

         String line = null;

         while (true) {
            System.out.print("Enter text: ");
            line = br.readLine();

            out.writeUTF(line);
         }

         // socket.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }


   Client(String host, int port) throws IOException {
      this.socket = new Socket(
         host,
         port
      );
   }


   public static void main(String [] args) {
      try {
         String host = args[0];
         int port = Integer.parseInt(args[1]);

         Client client = new Client(
            host, 
            port
         );

         Thread messageConsumerThread = new Thread(() -> client.consumeMessages());
         Thread messageSenderThread = new Thread(() -> client.sendMessages());

         messageConsumerThread.start();
         messageSenderThread.start();

         // client.sendMessages();
      }  catch (IOException e) {
         e.printStackTrace();
      }
   }
}