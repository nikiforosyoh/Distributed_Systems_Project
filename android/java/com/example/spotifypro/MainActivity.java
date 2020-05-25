package com.example.spotifypro;

import androidx.appcompat.app.AppCompatActivity;
import com.example.spotifypro.MusicFile;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

//it's the first activity ->welcome screen that durates SPLASH_TIME_OUT
public class MainActivity extends AppCompatActivity {
    //the time that exists
    private static  int SPLASH_TIME_OUT=3000;//3 seconds



    Button tap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent connectIntent = new Intent(MainActivity.this, ArtistList.class);
                startActivity(connectIntent);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}
