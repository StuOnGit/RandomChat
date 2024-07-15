package com.example.myapplication.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Room implements Parcelable {

    private String name;
    private int users;
    private int index;

    public Room(String name){
        this.name = name;
    }

    protected Room(Parcel in) {
        name = in.readString();
        users = in.readInt();
        index = in.readInt();
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    public String getName() {
        return name;
    }

    public int getUsers() {
        return users;
    }

    public int getIndex() {
        return index;
    }

    public void setUsers(int users) {
        this.users = users;
    }

    public void setIndex(int index){
        this.index = index;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(users);
        dest.writeInt(index);
    }
}
