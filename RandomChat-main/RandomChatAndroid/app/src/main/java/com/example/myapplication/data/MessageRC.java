package com.example.myapplication.data;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageRC {
    private String msg;
    private String timeMsgSent;
    private String typeMessage;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public MessageRC(String msg, String typeMessage){
        this.msg = msg;
        this.typeMessage = typeMessage;

        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        String dateString =  dateTime.format(formatter);
        this.timeMsgSent = dateString;

    }

    public String getTypeMessage() {
        return typeMessage;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTimeMsgSent() {
        return timeMsgSent;
    }

    public void setTimeMsgSent(String timeMsgSent) {
        this.timeMsgSent = timeMsgSent;
    }
}
