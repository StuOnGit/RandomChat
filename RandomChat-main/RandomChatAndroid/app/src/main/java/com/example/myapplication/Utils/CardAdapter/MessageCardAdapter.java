package com.example.myapplication.Utils.CardAdapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.MessageRC;
import com.example.myapplication.data.TypeOfMessage;

import java.util.ArrayList;

public class MessageCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    ArrayList<MessageRC> messages;

    public MessageCardAdapter(Activity activity, ArrayList<MessageRC> messages){
        this.activity = activity;
        this.messages = messages;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case 0:
                View view = LayoutInflater.from(activity).inflate(R.layout.item_container_sent_message, parent,false);
                return new SentMessageViewHolder(view);
            case 2:
                View view2 = LayoutInflater.from(activity).inflate(R.layout.item_container_received_message,parent, false);
                return new ReceivedMessageViewHolder(view2);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageRC messageRC = messages.get(position);
        switch (holder.getItemViewType()){
            case 0: //sentMessage
                SentMessageViewHolder viewHolder = (SentMessageViewHolder) holder;
                viewHolder.msgTextView.setText(messageRC.getMsg());
                viewHolder.timeMsgSentView.setText(messageRC.getTimeMsgSent());
                break;
            case 2: //receivedMessage
                ReceivedMessageViewHolder viewHolder2 = (ReceivedMessageViewHolder) holder;
                viewHolder2.msgTextView.setText(messageRC.getMsg());
                viewHolder2.timeMsgSentView.setText(messageRC.getTimeMsgSent());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).getTypeMessage().equals(TypeOfMessage.Sent)){
            return 0;
        }else{
            return 2;
        }
    }

    class SentMessageViewHolder extends RecyclerView.ViewHolder{

        private TextView msgTextView;
        private TextView timeMsgSentView;


        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            msgTextView = itemView.findViewById(R.id.text_message_sent);
            timeMsgSentView = itemView.findViewById(R.id.time_msg_received);
        }
    }

    class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{

        private TextView msgTextView;
        private TextView timeMsgSentView;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            msgTextView = itemView.findViewById(R.id.text_message_received);
            timeMsgSentView = itemView.findViewById(R.id.time_msg_sent);
        }
    }
}
