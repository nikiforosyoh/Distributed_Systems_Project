package com.example.spotifypro;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;

//First activity -> welcome screen with duration SPLASH_TIME_OUT
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
