package com.example.spotifypro;

import androidx.appcompat.app.AppCompatActivity;
import com.example.spotifypro.MusicFile;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
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
        //Button searchButton=(Button) findViewById(R.id.searchButton);
        ImageButton searchImage=(ImageButton)findViewById(R.id.searchImage);
        final EditText ArtistName=(EditText)findViewById(R.id.ArtistName);
        final EditText SongTitle=(EditText)findViewById(R.id.SongTitle);


        // perform click event on button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyFirstAsyncTask first=new MyFirstAsyncTask();
                first.execute();

            }
        });
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("myTag", "This is my message");
                //openConsumer με Async Task
                if(ArtistName.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
                }else if(SongTitle.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d("Check the control","Done");
                }
                MySecondTask second=new MySecondTask();
                String requestArtist=ArtistName.getText().toString().trim();
                String requestSong=SongTitle.getText().toString().trim();
                second.execute(requestArtist,requestSong);

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

     private class MySecondTask extends AsyncTask<String,Consumer,Consumer>{
        TextView searching=(TextView)findViewById(R.id.searching);


        @Override
        protected void onPreExecute() {

            Log.d("....","Searching for a song ");
            searching.setVisibility(View.VISIBLE);
        }

        @Override
        protected Consumer doInBackground(String...arg0) {
            Log.d("myTag5", "Second");
            String requestArt=arg0[0];
            String requestS=arg0[1];
            Log.d("Artist",requestArt);
            Log.d("Song",requestS);

            try {
                cons.openConsumer(requestArt,requestS);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("myTag6", "Second-3");
            return cons;
        }

        @Override
        protected void onPostExecute(Consumer co) {
            super.onPostExecute(co);
            Toast.makeText(MainActivity.this, "Second stage done", Toast.LENGTH_SHORT).show();
            Intent  intent=new Intent(MainActivity.this,MusicPlay.class);
            startActivity(intent);
        }
    }

}
