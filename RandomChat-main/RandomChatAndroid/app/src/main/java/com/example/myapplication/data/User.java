package com.example.myapplication.data;

import org.jetbrains.annotations.NotNull;

public class User {
    private String nickname;

    public User(@NotNull String nickname){
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
