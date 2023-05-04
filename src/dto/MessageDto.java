package src.dto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    private void writeObject(ObjectOutputStream out) throws IOException {
       out.writeObject(text);
       out.writeObject(text);
   }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
       text = (String) in.readObject();
       room = (String) in.readObject();
   }
 }