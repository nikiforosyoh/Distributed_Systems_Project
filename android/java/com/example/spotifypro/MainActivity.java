package com.example.spotifypro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

//First activity -> welcome screen with duration SPLASH_TIME_OUT
public class MainActivity extends AppCompatActivity {
    //the time that exists
    private static  int SPLASH_TIME_OUT=1000;//3 seconds



    Button tap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println(isOnline());
                if(isOnline()) {
                    Intent connectIntent = new Intent(MainActivity.this, ArtistList.class);
                    startActivity(connectIntent);
                    finish();
                }
                else{
                    Toast.makeText(MainActivity.this, "No Internet Connection.Go to Downloads file!", Toast.LENGTH_SHORT).show();
                    Intent connectIntent = new Intent(MainActivity.this, NoInternetCon.class);
                    startActivity(connectIntent);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);


    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
