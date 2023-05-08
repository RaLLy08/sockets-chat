package src.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import src.client.ChatCommandHandler.Command;

public class ConsoleReader extends Thread {
    ConsoleSubsriber consoleSubsriber;
 
    public static abstract class ConsoleSubsriber {
       void onInputText(String command) {};
       void onInputCommand(Command chatCommand, String param) {}
    }
    
    ConsoleReader(ConsoleSubsriber consoleSubsriber) {
       this.consoleSubsriber = consoleSubsriber;
    }
 
    @Override
    public void run() {
       try {
          BufferedReader br = new BufferedReader(
             new InputStreamReader(System.in)
          );
 
          while (true) {
             String text = br.readLine();
 
             if (text == null) continue;
 
             this.handleInputText(text);
          }
       } catch (IOException e) {
          e.printStackTrace();
       }
    }
 
    private void handleInputText(String text) {
       if (ChatCommandHandler.isCommand(text)) {
          this.onCommand(text);
          return;
       }
    
       this.consoleSubsriber.onInputText(text);
    }
 
    private void onCommand(String text) {
       ChatCommandHandler chatCommandHandler = new ChatCommandHandler(text);
   
       this.consoleSubsriber.onInputCommand(
          chatCommandHandler.getCommand(),
          chatCommandHandler.getParam()
       );
    }
 }
