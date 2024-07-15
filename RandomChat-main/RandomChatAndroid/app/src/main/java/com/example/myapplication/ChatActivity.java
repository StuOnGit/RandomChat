package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.myapplication.ControllerView.MainController;
import com.example.myapplication.Utils.CardAdapter.MessageCardAdapter;
import com.example.myapplication.Utils.CardAdapter.RoomCardAdapter;
import com.example.myapplication.Utils.Commands;
import com.example.myapplication.Utils.ErrorType;
import com.example.myapplication.Utils.Exception.ConnectionException;
import com.example.myapplication.Utils.Exception.IllegalCommandException;
import com.example.myapplication.Utils.Exception.RoomNotFoundException;
import com.example.myapplication.Utils.dialog.ErrorDialog;
import com.example.myapplication.Utils.dialog.SearchUserDialog;
import com.example.myapplication.data.MessageRC;
import com.example.myapplication.data.Room;
import com.example.myapplication.data.RoomI;
import com.example.myapplication.data.TypeOfMessage;
import com.example.myapplication.data.User;

import java.io.IOException;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements RoomI {


    private MainController mainController = new MainController();
    private SearchUserDialog dialog;
    private TextView textName;
    private EditText inputMessageEditText;
    private String messageToSend;
    private RecyclerView recyclerView;
    private MessageCardAdapter messageCardAdapter;
    private Room room;
    private FrameLayout sendButton;
    private ArrayList<MessageRC> messages = new ArrayList<>();
    private User chatUser;
    private SpeechRecognizer speech = null;
    private AppCompatImageView sendIcon;
    private boolean settedMic = true;
    private long longClick;
    private final Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    private TextView register_points;
    private FrameLayout register_layout;
    private AppCompatImageView register_icon;
    private AppCompatImageView send_icon;
    private AppCompatImageView back_icon;
    private AppCompatImageView info_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle extras;
        if(savedInstanceState == null){
            extras = getIntent().getExtras();
            if(extras == null){
                runOnUiThread(()->new ErrorDialog(this).startDialog(ErrorType.NullPointer, "Bundle extras."));
            }else{
                room = extras.getParcelable("Room");
                setUpViews();
                searchUsers();
            }
        }
    }


    private void setUpViews(){
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        register_layout = findViewById(R.id.registering_frame);
        register_layout.setVisibility(View.INVISIBLE);

        register_icon = findViewById(R.id.registering_icon);

        send_icon = findViewById(R.id.send_button_icon);

        back_icon = findViewById(R.id.back_chat_icon);
        back_icon.setOnClickListener(v->exitDialog());
        info_icon = findViewById(R.id.info_chat_icon); //TODO: se riesci fai anche una info per sapere nelle altre stanze quanta gente c'è e con chi sta parlando.
        register_points = findViewById(R.id.points_registering);
        textName = findViewById(R.id.textName);
        textName.setText("Room " +room.getName()+": ");
        sendButton =  findViewById(R.id.layoutSend);
        inputMessageEditText = findViewById(R.id.input_message);
        recyclerView = findViewById(R.id.messages_recycler_view);
        messageCardAdapter = new MessageCardAdapter(ChatActivity.this, messages);
        recyclerView.setAdapter(messageCardAdapter);

        sendIcon = sendButton.findViewById(R.id.send_button_icon);
        sendButton.setOnTouchListener(registerMsg());
        messageCardAdapter.notifyDataSetChanged();
        inputMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty()){
                    if(settedMic){
                        micChange();
                    }
                }else{
                    if(!settedMic){
                        micChange();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        speech.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                System.out.println("qua ci entro");

                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                    if(matches != null){
                        inputMessageEditText.setText(matches.get(0));
                    }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
    }

    private void searchUsers(){
        dialog = new SearchUserDialog(this, "Click me if you want to stop searching!",()->{
            mainController.stopWaiting();
        });
        dialog.startDialog();
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mainController.enterRoom(room, ChatActivity.this);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    runOnUiThread(()->new ErrorDialog(ChatActivity.this).startDialog(ErrorType.NullPointer, ioException.getMessage()));
                } catch (RoomNotFoundException roomNotFoundException){
                    runOnUiThread(()->new ErrorDialog(ChatActivity.this).startDialog(ErrorType.RoomNotFound, roomNotFoundException.getMessage()));
                } catch (ConnectionException e) {
                    e.printStackTrace();
                    runOnUiThread(()-> new ErrorDialog(ChatActivity.this).startDialog(ErrorType.Network, e.getMessage()));
                }
            }
        });
    }

    private View.OnTouchListener registerMsg(){
        return new View.OnTouchListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(settedMic){
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            longClick = event.getEventTime();
                            System.out.println(longClick);
                            return true;
                        case MotionEvent.ACTION_UP:
                            System.out.println("Action UP: " +(event.getEventTime() - longClick));
                            if((event.getEventTime() - longClick) >= 500.0){
                                speakToMe();
                                inputMessageEditText.setEnabled(false);
                                register_layout.setVisibility(View.VISIBLE);

                                send_icon.setColorFilter(Color.GREEN);
                                YoYo.with(Techniques.Shake).duration(1000).repeat(YoYo.INFINITE).playOn(register_points);
                            }else{
                                inputMessageEditText.setEnabled(true);
                                register_layout.setVisibility(View.INVISIBLE);
                                send_icon.setColorFilter(Color.WHITE);
                                speech.stopListening();
                            }
                            return true;
                    }
                }else{
                    messageToSend = String.valueOf(inputMessageEditText.getText());
                    if(messageToSend.isEmpty()){

                    }else{
                        AsyncTask.THREAD_POOL_EXECUTOR.execute(()->{
                            mainController.sendMsg(messageToSend);
                        });
                        MessageRC messageRC = new MessageRC(messageToSend, TypeOfMessage.Sent);
                        messages.add(messageRC);
                        inputMessageEditText.setText("");
                        messageCardAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messages.size()-1);
                    }
                }
                return false;
            }
        };
    }



    //TODO: fare lo speechRecognizer e vedere perchè non si aggiungono i messaggi

    @Override
    public void onUserFound(User user) {
        this.chatUser = user;
        dialog.dismissDialog();
        String titleChat = textName.getText().toString()+user.getNickname();
        runOnUiThread(()->textName.setText(titleChat));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(String msg) {
        MessageRC messageRC = new MessageRC(msg, TypeOfMessage.Received);
        messages.add(messageRC);
        runOnUiThread(()->{
            messageCardAdapter.notifyDataSetChanged();
        });
        runOnUiThread(()->{
            recyclerView.scrollToPosition(messages.size()-1);
        });
    }

    @Override
    public void onExit() {
        //quando l'utente 2 finisce la conversazione
        AsyncTask.THREAD_POOL_EXECUTOR.execute(()->{
            mainController.exitRoom();
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("User " + chatUser.getNickname() + " left the chat!");
        builder.setCancelable(false);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        runOnUiThread(()->{
            Dialog dialog = builder.create();
            dialog.show();
        });
    }

    @Override
    public void onBackPressed() {
        exitDialog();
    }



    private void exitDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChatActivity.this);
        alertDialog.setMessage("Leave the chat?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    AsyncTask.THREAD_POOL_EXECUTOR.execute(()->{
                        mainController.exitRoom();
                    });
                    ChatActivity.super.onBackPressed();
                    finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }


    private void speakToMe(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
            speech.startListening(recognizerIntent);
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RoomCardAdapter.AUDIO_RECORD_REQUEST_CODE);
        }
    }

    private void micChange(){
        if(settedMic){
            sendIcon.setImageResource(R.drawable.ic_outline_send_24);
        }else{
            sendIcon.setImageResource(R.drawable.ic_baseline_mic_24);
        }
        settedMic = !settedMic;
    }


}