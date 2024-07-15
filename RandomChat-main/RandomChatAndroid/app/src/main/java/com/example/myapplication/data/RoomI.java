package com.example.myapplication.data;

public interface RoomI {
    void onUserFound(User user);
    void onMessageReceived(String msg);
    void onExit();
}
