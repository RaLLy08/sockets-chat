package src.dto;

import java.io.Serializable;

public class MessageDto implements Serializable {
   public String text;
   public String room;

   public MessageDto(String room, String text) {
      this.room = room;
      this.text = text;
   }

   public MessageDto(String room) {
      this.room = room;
   }

   public String getRoom() {
      return room;
   }

   public String getText() {
      return text;
   }
}
