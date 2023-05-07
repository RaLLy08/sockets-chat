package src.dto;

import java.io.Serializable;

public class MessageDto implements Serializable {
   public static enum Action {
      JOIN,
      LEAVE,
      MESSAGE,
      ERROR
   }

   public String text;
   public String room;
   public Action action;
   public String remoteSocketAddress;

   public MessageDto(String room, String text, String remoteSocketAddress) {
      this.room = room;
      this.text = text;
      this.remoteSocketAddress = remoteSocketAddress;
   }

   public MessageDto(String room, String text, Action action) {
      this.room = room;
      this.text = text;
      this.action = action;
   }

   public MessageDto(String room, Action action) {
      this.room = room;
      this.action = action;
   }

   public MessageDto(String room, String text) {
      this.room = room;
      this.text = text;
   }

   public MessageDto(String text) {
      this.text = text;
   }

   public String getRoom() {
      return room;
   }

   public String getText() {
      return text;
   }

   public MessageDto addAction(Action action) {
      this.action = action;
      return this;
   }
}

