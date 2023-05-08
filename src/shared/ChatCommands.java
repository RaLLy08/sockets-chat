package src.shared;

public class ChatCommands {
    /*
     * /join <room> - join or create room
     */
    public static final String JOIN = "/join";
    /*
     * /quite - leave room
     */
    public static final String QUIT = "/quite";
    /*
     * /roomlist - get list of rooms
     */
    public static final String ROOM_LIST = "/roomlist";
    /*
     * /userslist - get list of users in room
     */
    public static final String USER_LIST = "/userlist";

    public static String getDocs() {
        return String.format("%s <room> - join or create room\n%s - leave room\n%s - get list of rooms\n%s - get list of users in room", JOIN, QUIT, ROOM_LIST, USER_LIST);
    }
}