package models;

import java.util.ArrayList;
import java.util.UUID;


public class Room extends ArrayList<User> {
    int id;
    String name;

    public Room(String name) {
        this.id = UUID.randomUUID().hashCode();
        this.name = name;
    }
}
