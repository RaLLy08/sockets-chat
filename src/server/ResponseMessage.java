package src.server;

import src.shared.ChatCommands;
import src.shared.MessageDto;

public class ResponseMessage {
    public static MessageDto newRoomCreated(String room) {
       return new MessageDto(
            String.format("New room '" + room + "' has been created, write '%s' to leave the room", ChatCommands.QUIT)
       )
          .addAction(MessageDto.Action.JOIN);
    }
 
    public static MessageDto joinedRoom(String room) {
       return new MessageDto("You have joined the room '" + room + "'")
          .addAction(MessageDto.Action.JOIN);
    }
 
    public static MessageDto leftRoom() {
       return new MessageDto("You have left the room")
          .addAction(MessageDto.Action.LEAVE);
    }
 
    public static MessageDto clientAlreadyInLobby() {
       return new MessageDto("You are already in the lobby")
          .addAction(MessageDto.Action.ERROR);
    }
 
    public static MessageDto clientAlreadyInRoom(String room) {
       return new MessageDto("You are already in the room '" + room + "'")
          .addAction(MessageDto.Action.ERROR);
    }
 
    public static MessageDto roomNotProvided() {
       return new MessageDto("Room is not provided")
          .addAction(MessageDto.Action.ERROR);
    }
 
    public static MessageDto roomAlreadySet() {
       return new MessageDto("You are already in a room")
          .addAction(MessageDto.Action.ERROR);
    }
 
    public static MessageDto notInRoom() {
       return new MessageDto("You are not in a room")
          .addAction(MessageDto.Action.ERROR);
    }
 
    public static MessageDto invalidAction() {
       return new MessageDto("Unknown message type")
          .addAction(MessageDto.Action.ERROR);
    }
 
    public static MessageDto broadcastJoinRoom(String remoteSocketAddress, int size) {
       String message = String.format(
          "%s Joined to room. Number of users in room: %d", 
          remoteSocketAddress,
          size
       );
 
       return new MessageDto(message).addAction(MessageDto.Action.JOIN);
    }
 
    public static MessageDto broadcastLeaveRoom(String remoteSocketAddress, int size) {
       String message = String.format(
          "%s Left the room. Number of users in room: %d", 
          remoteSocketAddress,
          size
       );
 
       return new MessageDto(message).addAction(MessageDto.Action.LEAVE);
    }

    public static MessageDto listRooms(String rooms) {
       return new MessageDto("List of rooms: " + rooms)
          .addAction(MessageDto.Action.GET_ROOM_LIST);
    }

    public static MessageDto listRoomMembers(String members) {
       return new MessageDto("List of members: " + members)
          .addAction(MessageDto.Action.GET_ROOM_MEMBERS);
    }
 }
