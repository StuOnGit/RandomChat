package com.example.myapplication.Utils.CardAdapter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ChatActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.data.Room;

import java.util.ArrayList;

public class RoomCardAdapter extends RecyclerView.Adapter<RoomCardAdapter.ViewHolder> {

    ArrayList<Room> rooms;
    Activity activity;
    public static final int AUDIO_RECORD_REQUEST_CODE = 12;


    public RoomCardAdapter(Activity activity, ArrayList<Room> rooms){
        this.activity = activity;
        this.rooms = rooms;
    }

    public void clear(){
        int size = rooms.size();
        rooms.clear();
        notifyItemRangeChanged(0,size);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity.getApplicationContext());
        View view = inflater.inflate(R.layout.room_card, parent, false);
        return new RoomCardAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.usersInRoom.setText("Users: " + String.valueOf(rooms.get(position).getUsers()));
        holder.roomTitle.setText(rooms.get(position).getName());
        holder.setRoom(rooms.get(position));
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView roomTitle, usersInRoom;
        private Room room;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            roomTitle = itemView.findViewById(R.id.room_title_text_view);
            usersInRoom = itemView.findViewById(R.id.users_in_room_text_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
                        Intent intent = new Intent(activity, ChatActivity.class);
                        intent.putExtra("Room", room);
                        activity.startActivity(intent);
                    }else{
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_RECORD_REQUEST_CODE);
                    }

                }
            });
        }

        public void setRoom(Room room) {
            this.room = room;
        }


    }
}
