package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ControllerView.MainController;
import com.example.myapplication.Utils.CardAdapter.RoomCardAdapter;
import com.example.myapplication.Utils.Exception.ConnectionException;
import com.example.myapplication.Utils.dialog.ErrorDialog;
import com.example.myapplication.Utils.ErrorType;
import com.example.myapplication.Utils.dialog.LoadingDialog;
import com.example.myapplication.data.Room;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MainController mainController = new MainController();
    private RoomCardAdapter roomCardAdapter;
    private TextView titleTextView;
    private TextView userNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "starting the activity!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpViews();
        connect();
    }

    private void setUpViews(){
        recyclerView = findViewById(R.id.room_recycler_view);
        swipeRefreshLayout = findViewById(R.id.swiperefreshlayout);
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
        titleTextView = findViewById(R.id.app_name_text_main);
        Shader shader = new LinearGradient(0f, 0f, 0f, titleTextView.getTextSize(), Color.RED, Color.YELLOW, Shader.TileMode.CLAMP);
        titleTextView.getPaint().setShader(shader);
        userNameTextView = findViewById(R.id.user_name_main);
        userNameTextView.setText(((RCApplication)getApplicationContext()).user.getNickname());
    }

    private void connect(){
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    mainController.connect();
                    mainController.sendNickname(RCApplication.user.getNickname());
                    AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ArrayList<Room> rooms = mainController.getRooms();
                                runOnUiThread(()->{
                                    roomCardAdapter = new RoomCardAdapter(MainActivity.this, rooms);
                                    recyclerView.setAdapter(roomCardAdapter);
                                });
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    });


                    runOnUiThread(()->{
                        loadingDialog.dismissDialog();
                    });
                }catch (IOException ioException){
                    Log.e(TAG, ioException.getLocalizedMessage());

                    runOnUiThread(() -> {
                        loadingDialog.dismissDialog();
                        new ErrorDialog(MainActivity.this).startDialog(ErrorType.Network, ioException.getMessage());

                    });
                }catch (ConnectionException e) {
                    e.printStackTrace();
                    runOnUiThread(()-> new ErrorDialog(MainActivity.this).startDialog(ErrorType.Network, e.getMessage()));
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
       exitDialog();
    }

    private void exitDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setMessage("Do you want to close the session?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.super.onBackPressed();
                try {
                    mainController.closeConnection();
                    finish();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Continue to random chat!", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();
    }

    private void refreshData(){
        roomCardAdapter.clear();
        recyclerView.setAdapter(roomCardAdapter);
        AsyncTask.THREAD_POOL_EXECUTOR.execute(()->{
            try {
                ArrayList<Room> rooms = mainController.getRooms();
                runOnUiThread(()->{
                    roomCardAdapter = new RoomCardAdapter(this, rooms);
                    recyclerView.setAdapter(roomCardAdapter);
                    swipeRefreshLayout.post(()->swipeRefreshLayout.setRefreshing(false));

                });
            } catch (IOException ioException) {
                ioException.printStackTrace();
                runOnUiThread(()->new ErrorDialog(this).startDialog(ErrorType.Network, ioException.getMessage()));
                swipeRefreshLayout.post(()->swipeRefreshLayout.setRefreshing(false));
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshData();
    }
}