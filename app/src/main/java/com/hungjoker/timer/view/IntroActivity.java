package com.hungjoker.timer.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungjoker.timer.R;

import java.util.Timer;
import java.util.TimerTask;

public class IntroActivity extends AppCompatActivity {

    private ImageView imgIntro;
    private TextView txtCopyright;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        imgIntro = (ImageView) findViewById(R.id.imgIntro);
        txtCopyright = (TextView) findViewById(R.id.txtCopyright);
        Animation introAnimation = AnimationUtils.loadAnimation(this, R.anim.intro_anim);
        imgIntro.startAnimation(introAnimation);
        txtCopyright.startAnimation(introAnimation);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(IntroActivity.this, HomeTimerActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3500);
    }
}
