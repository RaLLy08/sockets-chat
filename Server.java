import java.net.*;
import java.io.*;
// import models.User;

public class Server extends Thread {
   private ServerSocket serverSocket;
   private Socket server;

   // public Server(int port) throws IOException {
   //    this.serverSocket = new ServerSocket(port);
   //    // serverSocket.setSoTimeout(10000);
   // }

   // public void run() {
   //    while(true) {
   //       try {
   //          System.out.println(
   //             "Waiting for clients" + 
   //             this.serverSocket.getLocalPort()
   //          );
            
   //          // wait connect to client
   //          this.server = this.serverSocket.accept();
            
   //          System.out.println("Client connected: " + server.getRemoteSocketAddress());
        
   //          Thread clientHandlerThread = new ClientHandler(this.server);

   //          clientHandlerThread.start();


   //          // this.serverSocket.close();
   //          // this.server.close();
   //       } catch (IOException e) {
   //          e.printStackTrace();
   //          break;
   //       }
   //    }
   // }
   
   public static void main(String [] args) {
      int port = Integer.parseInt(args[0]);

      ServerSocket serverSocket;

      try {
         serverSocket = new ServerSocket(port);
   
         while(true) {

            System.out.println(
               "Waiting for clients" + 
               serverSocket.getLocalPort()
            );
               
            // wait connect to client
            Socket socket = serverSocket.accept();
               
            System.out.println("Client connected: " + socket.getRemoteSocketAddress());
           
            Thread clientHandlerThread = new ClientHandler(socket);
   
            clientHandlerThread.start();
         }

      } catch (IOException e) {
         
         e.printStackTrace();
      }
   }
}


class ClientHandler extends Thread {
   private Socket socket;
   private DataInputStream in;
   private DataOutputStream out;
   // private User user;

   ClientHandler(Socket socket) throws IOException {
      this.socket = socket;
      this.in = new DataInputStream(
         this.socket.getInputStream()
      );
      this.out = new DataOutputStream(
         this.socket.getOutputStream()
      );
   }

   public void run() {
      try {
         System.out.println("Connected to: " + this.socket.getRemoteSocketAddress());

         // while (true) {
         //    String recievedLine = this.in.readUTF();

         //    System.out.println(
         //       recievedLine
         //    );

         //    this.out.writeUTF("Hello" + this.socket.getLocalSocketAddress() + "Bye!");
         // }

         String inputLine;

         while ((inputLine = this.in.readUTF()) != null) {
             System.out.println(
               "Received from client: " + 
               this.socket.getRemoteSocketAddress() + 
               "\n" + 
               inputLine
            );
         }

         System.out.println("Client disconnected.");
         
         in.close();
         this.socket.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}