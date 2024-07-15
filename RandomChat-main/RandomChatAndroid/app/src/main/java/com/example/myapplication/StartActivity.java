package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ControllerView.StartController;
import com.example.myapplication.Utils.MyBounceInterpolator;
import com.example.myapplication.data.User;

public class StartActivity extends AppCompatActivity {

    private TextView titleTextView;
    private Button startButton;
    private EditText nicknameEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setUpActivityViews();
        setUpActions();
    }

    private void setUpActivityViews(){
        titleTextView = findViewById(R.id.title_text_view);
        Shader shader = new LinearGradient(0f, 0f, 0f, titleTextView.getTextSize(), Color.RED, Color.YELLOW, Shader.TileMode.CLAMP);
        titleTextView.getPaint().setShader(shader);

        startButton = findViewById(R.id.start_button);
        nicknameEditText = findViewById(R.id.nickname_edit_text);
    }

    private void setUpActions(){
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        tapToAnimate(startButton);
                        String ret = new StartController().checkNickname(nicknameEditText.getText().toString());
                            if(ret != null){
                                nicknameEditText.setError(ret);
                            }else{
                                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                                ((RCApplication)getApplication()).user = new User(nicknameEditText.getText().toString());
                                startActivity(intent);
                            }
                }
            });
    }

    private void tapToAnimate(View view){
        Button button = (Button) view;

        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        animation.setInterpolator(interpolator);
        button.startAnimation(animation);
    }
}