package com.example.myapplication.ControllerView;

import com.example.myapplication.Connection.ConnectionController;
import com.example.myapplication.Utils.Commands;
import com.example.myapplication.Utils.Exception.ConnectionException;
import com.example.myapplication.Utils.Exception.IllegalCommandException;
import com.example.myapplication.Utils.Exception.RoomNotFoundException;
import com.example.myapplication.data.Room;
import com.example.myapplication.data.RoomI;
import com.example.myapplication.data.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Consumer;

public class MainController {

    ChatThread chatThread;
    private ConnectionController connectionController = ConnectionController.getInstance();
    boolean userChatting = false;

    public ArrayList<Room> getRooms() throws IOException{
        ArrayList<Room> rooms = null;
        if(connectionController != null && connectionController.isOpen()){
            connectionController.send(Commands.GET_ROOMS, "");
            int i = Integer.parseInt(connectionController.read());
            rooms = new ArrayList<>();
            for(int j = 0; j < i; j++){
                String roomStr = connectionController.read();
                Scanner scanner = new Scanner(roomStr);
                String roomName = scanner.next();
                int userCount = scanner.nextInt();
                Room room = new Room(roomName);
                room.setUsers(userCount);
                room.setIndex(j);
                rooms.add(room);
            }
        }
        return rooms;
    }

    public void connect() throws IOException{
        connectionController.connect();
    }

    public void sendNickname(String nickname) throws ConnectionException {
        if(connectionController != null && connectionController.isOpen()){
            if(!nickname.isEmpty()){
                nickname = nickname.trim();
                nickname = nickname + '\0';
                connectionController.send(Commands.SET_NICKNAME, nickname);
            }
        }else{
            throw new ConnectionException("Connection not open");
        }
    }

    public void closeConnection() throws IOException{
        connectionController.closeConnection();
    }

    public void enterRoom(Room room, RoomI roomI) throws IOException, RoomNotFoundException, ConnectionException {
        if(connectionController != null && connectionController.isOpen()){
            connectionController.send(Commands.ENTER_IN_ROOM, String.valueOf(room.getIndex()));
        }else{
            throw new ConnectionException("Connection not open");
        }

        do{
            String msg = connectionController.read();
            if(msg != null){
                msg = msg.replace('\n', '\0');
                switch(String.valueOf(msg.charAt(0))){
                    case Commands.STOP_SEARCH:
                        return;
                    case Commands.EXIT:
                        throw new RoomNotFoundException("Room not found, index error!");
                    default:
                        roomI.onUserFound(new User(getName("[",msg, "]")));
                        chatThread = new ChatThread(roomI);
                        chatThread.start();
                        userChatting = true;
                        return;
                }
            }
        }while(true);
    }

    private String getName(CharSequence charStart, String msg, CharSequence charEnd){
        String ret = null;
        if(msg.contains(charStart) && msg.contains(charEnd)){
            if(msg.indexOf(String.valueOf(charStart)) < msg.indexOf(String.valueOf(charEnd))){
                ret = msg.substring(msg.indexOf(String.valueOf(charStart))+1, msg.indexOf(String.valueOf(charEnd)));
            }
        }
        return ret;
    }

    public void stopWaiting(){
        if(connectionController != null && connectionController.isOpen()){
            connectionController.send(Commands.STOP_WAITING,"");
        }
    }

    public void sendMsg(String msg){
        if(connectionController != null && connectionController.isOpen()){
            connectionController.send(Commands.SEND_MSG, msg+'\n');
        }
    }

    public void exitRoom(){
        if(!userChatting){
            if(connectionController != null && connectionController.isOpen()){
                connectionController.send(Commands.EXIT, "\n");
            }
        }else {
            userChatting = false;
            connectionController.send(Commands.EXIT, "\n");
            try {
                chatThread.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }

        }

    }

    public String read() throws IOException {
        String ret = null;
        if(connectionController != null && connectionController.isOpen()){
            ret = connectionController.read();
        }
        return ret;
    }



    private class  ChatThread extends Thread{
        RoomI listener;

        public ChatThread(RoomI listener){
            this.listener = listener;
        }

        @Override
        public void run() {
            String msgReceived = " ";
            while(!String.valueOf(msgReceived.charAt(0)).equals(Commands.EXIT) || userChatting){
                try {
                    msgReceived = read();
                    System.out.println(msgReceived);
                    String command = String.valueOf(msgReceived.charAt(0));
                    if(command.equals(Commands.EXIT)){
                        if(!userChatting){
                            return;
                        }else{
                            listener.onExit();
                        }
                    }else if(command.equals(Commands.SEND_MSG)){
                        listener.onMessageReceived(msgReceived.substring(1)); //per togliere M
                    }else{
                        throw new IllegalCommandException("Unkown command: " + msgReceived.charAt(0));
                    }
                } catch (IOException | IllegalCommandException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

}
