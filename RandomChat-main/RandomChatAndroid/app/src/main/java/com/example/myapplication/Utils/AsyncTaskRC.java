package com.example.myapplication.Utils;

import android.app.Activity;
import android.os.AsyncTask;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ControllerView.MainController;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Utils.CardAdapter.RoomCardAdapter;
import com.example.myapplication.Utils.dialog.ErrorDialog;
import com.example.myapplication.data.Room;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;

public class AsyncTaskRC extends AsyncTask<URL, Integer, Long> {
    WeakReference recyclerView;
    WeakReference adapter;

    @Override
    protected Long doInBackground(URL... urls) {

        return null;
    }



    public AsyncTaskRC(RecyclerView recyclerView, RoomCardAdapter adapter){
        this.recyclerView = new WeakReference(recyclerView);
        this.adapter = new WeakReference(adapter);
    }
    @Override
    protected void onPostExecute(Long aLong) {
        super.onPostExecute(aLong);

       /* try{
            if(recyclerView.get() != null){
                //RecyclerView recyclerView = (RecyclerView) recyclerView.get();
            }*/
            //recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        /*}catch (IOException ioException){
            ioException.printStackTrace();
           // runOnUiThread(()->new ErrorDialog(this).startDialog(ErrorType.Network, ioException.getLocalizedMessage()));
        }*/
    }
}
