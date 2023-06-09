package src.client;

import src.shared.ChatCommands;

public class ChatCommandHandler {
    public static enum Command {
      JOIN,
      QUIT,
      ROOM_LIST,
      USER_LIST,
    }
    static public boolean isCommand(String text) {
       return text.startsWith("/");
    }
 
    String text;
    String[] parts;
 
    ChatCommandHandler(String text) {
       this.text = text;
 
       this.setParts();
    }
 
    private void setParts() {
       this.parts = this.text.split("\\s+");
    }
 
    Command getCommand() {
       String command = this.parts[0];
 
       if (command.equals(ChatCommands.JOIN)) {
          return Command.JOIN;
       } else if (command.equals(ChatCommands.QUIT)) {
          return Command.QUIT;
       } else if (command.equals(ChatCommands.ROOM_LIST)) {
          return Command.ROOM_LIST;
       } else if (command.equals(ChatCommands.USER_LIST)) {
          return Command.USER_LIST;
       }
      return null;
    }
 
    String getParam() {
       if (this.parts.length < 2) {
          return null;
       }
 
       return this.parts[1];
    }
    
 }
