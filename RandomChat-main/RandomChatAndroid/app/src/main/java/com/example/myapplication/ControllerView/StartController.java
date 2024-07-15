package com.example.myapplication.ControllerView;

import android.util.Log;

import java.util.regex.Pattern;

public class StartController {
    private static final String TAG = "StartController";

    public String checkNickname(String nickname){ //null = stringa corretta, altrimenti tipo di errore
        String retString = null;
        String regex = "[a-zA-Z]{0,15}";
        nickname = nickname.trim();
        if(nickname.isEmpty()){
            retString = "Empty nickname";
            Log.d(TAG, "checkNickname: "+ retString);
        }else {
            if (!Pattern.matches(regex, nickname)) {
                retString = "Nickname can contain only letters and max 15 letters";
                Log.d(TAG, "checkNickname: " + retString);
            }
        }
        return retString;
    }
}
