package com.example.myapplication.Utils.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

public class ErrorDialog {
    private Activity activity;
    private AlertDialog dialog;
    private ImageView closeButton;
    private TextView error_msg;
    private TextView title;


    public ErrorDialog(Activity activity){
        this.activity = activity;
    }

    public void startDialog(String title, String error_msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.error_dialog, null));

        dialog = builder.create();
        dialog.setCancelable(false);

        dialog.show();

        this.error_msg = dialog.findViewById(R.id.error_message);
        this.title = dialog.findViewById(R.id.title_error_dialog);
        this.error_msg.setText(error_msg);
        this.title.setText(title);

        closeButton = dialog.findViewById(R.id.close_error_dialog);
        enableCloseDialog();
    }

    private void enableCloseDialog(){
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });
    }
}
