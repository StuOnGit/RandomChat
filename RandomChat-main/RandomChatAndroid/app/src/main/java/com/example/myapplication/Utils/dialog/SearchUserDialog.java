package com.example.myapplication.Utils.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.myapplication.R;

public class SearchUserDialog {

    private Activity activity;
    private String answer;
    private Button answerText;
    private AlertDialog dialog;
    private Runnable runnable;

    public SearchUserDialog(Activity activity, String answer, Runnable runnable){
        this.activity = activity;
        this.answer = answer;
        this.runnable = runnable;
    }

    public void startDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.search_user_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);
        dialog = builder.create();
        answerText = ((Button) view.findViewById(R.id.search_button_exit));
        answerText.setText(answer);

        answerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    AsyncTask.THREAD_POOL_EXECUTOR.execute(runnable);
                    dialog.dismiss();
                    activity.finish();
            }
        });
        dialog.show();
    }


    public void dismissDialog(){
        if(dialog != null){
            dialog.dismiss();
        }
    }


}
