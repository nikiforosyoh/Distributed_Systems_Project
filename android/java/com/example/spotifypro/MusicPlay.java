package com.example.spotifypro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MusicPlay extends AppCompatActivity {
    Consumer cons = new Consumer("192.168.77.1", 5000);


    private ArrayList<MusicFile> listOfSongs = new ArrayList<MusicFile>();
    private ArrayList<MusicFile> listOfChunks = new ArrayList<>();
    private ArrayList<byte[]> listOfMusicExtraction = new ArrayList<>();
    private MediaPlayer mediaPlayer = new MediaPlayer();


    TextView songtitle, artist;
    ImageView album;
    ImageButton  searchImage,btnBack,btnForward,btnPlay;
    SeekBar seekBar;
    byte[] mp3fileforplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);


        songtitle = (TextView) findViewById(R.id.songtitle);
        artist = (TextView) findViewById(R.id.artist);
        searchImage = (ImageButton) findViewById(R.id.searchImage);
        btnPlay=(ImageButton)findViewById(R.id.btnPlayer);
        seekBar=(SeekBar)findViewById(R.id.seekbar);
        btnForward=(ImageButton)findViewById(R.id.btnForward);
        btnBack=(ImageButton)findViewById(R.id.btnBack);
        album=(ImageView)findViewById(R.id.album);
        final EditText SongTitle = (EditText) findViewById(R.id.SongTitle);


        //take the value of artist name form the Connect activity
        artist.setText(getIntent().getStringExtra("ARTIST"));

        //what search image button does
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SongTitle.getText().toString().isEmpty()) {
                    Toast.makeText(MusicPlay.this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Check the control", "Done");
                }
                MySecondTask second = new MySecondTask();
                String requestArtist = artist.getText().toString().trim();
                String requestSong = SongTitle.getText().toString().trim();
                second.execute(requestArtist, requestSong);

            }
        });



        btnPlay.setOnClickListener(new View.OnClickListener() {
            TextView currentTimer=(TextView)findViewById(R.id.currentTimer);
            @Override
            public void onClick(View v) {

                MyThirdTask third=new MyThirdTask();
                third.execute(mp3fileforplay);
                //currentTimer.setText(mediaPlayer.getDuration());
            }
        });


        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+5000);//duration of a chunk?
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-3000);//duration of a chunk?
            }
        });


    }


    private class MySecondTask extends AsyncTask<String, Consumer, byte[]> {

        TextView searching=(TextView)findViewById(R.id.searching);

        @Override
        protected void onPreExecute() {

            Log.d("....", "Searching for a song ");
            searching.setVisibility(View.VISIBLE);

        }

        @Override
        protected byte[] doInBackground(String... arg0) {
            Log.d("Insert", "Second stage");
            String requestArt = arg0[0];
            String requestS = arg0[1];
            Log.d("Artist", requestArt);
            Log.d("Song", requestS);
            cons.init(cons.getBrokerIp(),cons.getBrokerPort(),cons.getAvailableBrokers());
            Log.d("myTag3", "Problem2");
            cons.initialization();
            Log.d("myTag4", "Problem3");

            try {
               listOfChunks=cons.openConsumer(requestArt,requestS);
               listOfMusicExtraction=cons.takeMusicFileExtraction(listOfChunks.get(0).totalChunks,listOfChunks);
               mp3fileforplay=cons.recreateFile(listOfMusicExtraction);

            } catch (IOException e) {
                 e.printStackTrace();
            }
            Log.d("myTag6", "Second-3");
            return mp3fileforplay;
        }

        @Override
        protected void onPostExecute(byte [] b) {
            super.onPostExecute(b);
            btnPlay.setVisibility(View.VISIBLE);

        }

    }

    private class MyThirdTask extends AsyncTask<byte[],String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            seekBar.setVisibility(View.VISIBLE);
            btnForward.setVisibility(View.VISIBLE);
            btnForward.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.VISIBLE);
            songtitle.setVisibility(View.VISIBLE);
            artist.setVisibility(View.VISIBLE);
            album.setVisibility(View.VISIBLE);
            searchImage.setVisibility(View.INVISIBLE);

            songtitle.setText(listOfChunks.get(0).trackName);

        }

        @Override
        protected String doInBackground(byte[]... args) {
            byte[] mp3M=args[0];
            String r=listOfChunks.get(0).trackName;
            try {
                mediaPlayer=playMp3(mp3M,r);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return r;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }

    //play the song
    private MediaPlayer playMp3(byte[] mp3SoundByteArray,String title) throws IOException {
        File tempMp3file= File.createTempFile(title,"mp3",getCacheDir());
        tempMp3file.deleteOnExit();
        FileOutputStream fout=new FileOutputStream(tempMp3file);
        fout.write(mp3SoundByteArray);
        fout.close();

        mediaPlayer.reset();
        MediaPlayer mediaPlayer = new MediaPlayer();
        FileInputStream fis = new FileInputStream(tempMp3file);
        mediaPlayer.setDataSource(fis.getFD());

        mediaPlayer.prepare();
        mediaPlayer.start();
        return mediaPlayer;


    }
}


