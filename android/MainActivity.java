package com.example.spotifypro;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Consumer cons = new Consumer("192.168.77.1", 5000);//broker ip αλλάζει ανάλογα το pc
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //final ProgressBar simpleProgressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        Button startButton = (Button) findViewById(R.id.startButton);
        Button searchButton=(Button) findViewById(R.id.searchButton);
        final EditText ArtistName=(EditText)findViewById(R.id.ArtistName);
        final EditText SongTitle=(EditText)findViewById(R.id.SongTitle);

        // perform click event on button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ArtistName.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
                }else if(SongTitle.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
                }else{
                    //ArtistName->requestArtist SongTitle->requestSong
                    String requestArtist=ArtistName.getText().toString().trim();
                    String requestSong=SongTitle.getText().toString().trim();
                }
                MyFirstAsyncTask first=new MyFirstAsyncTask();
                first.execute();

            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("myTag", "This is my message");
                //openConsumer με Async Task


            }
        });

    }
    private class MyFirstAsyncTask extends AsyncTask<Consumer,Consumer,Consumer>{
        ProgressBar simpleProgressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            simpleProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Consumer doInBackground(Consumer...consumers) {
            Log.d("myTag2", "Problem");
            cons.init(cons.getBrokerIp(),cons.getBrokerPort(),cons.getAvailableBrokers());
            Log.d("myTag3", "Problem2");
            cons.initialization();
            Log.d("myTag4", "Problem3");
            return cons;
        }

        @Override
        protected void onPostExecute(Consumer co) {
            super.onPostExecute(co);
            Toast.makeText(MainActivity.this, "First stage done", Toast.LENGTH_SHORT).show();
        }
    }




}
