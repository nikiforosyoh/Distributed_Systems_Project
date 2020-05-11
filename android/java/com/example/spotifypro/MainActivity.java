package com.example.spotifypro;

import androidx.appcompat.app.AppCompatActivity;
import com.example.spotifypro.MusicFile;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;


public class MainActivity extends AppCompatActivity {
    //the time that exists
    private static  int SPLASH_TIME_OUT=3000;//3 seconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent connectIntent =new Intent(MainActivity.this,Connect.class);
                startActivity(connectIntent);
                finish();
            }
        },SPLASH_TIME_OUT);


    }
}
