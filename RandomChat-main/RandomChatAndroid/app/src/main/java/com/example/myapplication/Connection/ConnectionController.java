package com.example.myapplication.Connection;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionController {

    private static ConnectionController instance = null;
    private Socket socket = null;
    private BufferedReader inputStream = null;
    private OutputStream outputStream = null;
    private PrintWriter output = null;
    private final String address = "40.118.52.5"; //Azure
    //private final String address = "192.168.1.12";
    private int port = 8080;
    //private int port = 8080;
    private ConnectionController(){

    }

    public static ConnectionController getInstance(){
        if(instance == null){
            synchronized (ConnectionController.class){
                if(instance == null){
                    instance = new ConnectionController();
                }
            }
        }
        return instance;
    }

    public void connect() throws IOException{
        socket = new Socket(address, port);
        inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outputStream = socket.getOutputStream();
        output = new PrintWriter(outputStream);
    }


    public boolean isOpen(){
        if(socket != null){
            if(!socket.isClosed()){
                return true;
            }
        }
        return false;
    }

    public void closeConnection() throws IOException{
        if(isOpen()) {
            output.close();
            outputStream.close();
            socket.close();
            instance = null;
        }
    }

    public  void send(String command, String message){
        synchronized (output){
            output.write((command + message));
            output.flush();
        }
    }

    public String read() throws IOException{
        synchronized (inputStream){
            String msg = inputStream.readLine();
            if(msg != null){
                return msg;
            }
        }
        return null;
    }
}
