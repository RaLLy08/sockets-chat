package src.server.models;
import java.util.*;


public class Rooms {
    private Map<String, Room> rooms;

    static public class Room extends ArrayList<User> {
        int id;
        String name;
    
        public Room(String name) {
            this.id = UUID.randomUUID().hashCode();
            this.name = name;
        }
    }
    
    public Rooms() {
        rooms = new HashMap<String, Room>();
    }

    
    public Room getUsersInRoom(String roomName) {
        return rooms.get(roomName);
    }

    public void createRoom(String roomName) {
        Room users = rooms.get(roomName);

        if (users == null) {
            users = new Room(roomName);
            rooms.put(roomName, users);
        }
    }

    
    public void joinRoom(String roomName, User user) {
        Room users = rooms.get(roomName);

        users.add(user);
    }
}