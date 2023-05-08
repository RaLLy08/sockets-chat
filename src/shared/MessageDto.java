package src.shared;

import java.io.Serializable;

public class MessageDto implements Serializable {
   public static enum Action {
      JOIN,
      LEAVE,
      MESSAGE,
      ERROR,
      GET_ROOM_LIST,
      GET_ROOM_MEMBERS,
   }

   public String text;
   public String room;
   public Action action;
   public String remoteSocketAddress;

   public MessageDto(String room, String text, String remoteSocketAddress, Action action) {
      this.room = room;
      this.text = text;
      this.remoteSocketAddress = remoteSocketAddress;
      this.action = action;
   }

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

   public MessageDto(Action action) {
      this.action = action;
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

   public MessageDto addText(String text) {
      this.text = text;
      return this;
   }

   public MessageDto addRoom(String room) {
      this.room = room;
      return this;
   }

   public MessageDto addRemoteSocketAddress(String remoteSocketAddress) {
      this.remoteSocketAddress = remoteSocketAddress;
      return this;
   }

   public String toString() {
      return String.format(
         "MessageDto { room: %s, text: %s, action: %s, remoteSocketAddress: %s }",
         this.room,
         this.text,
         this.action,
         this.remoteSocketAddress
      );
   }
}

