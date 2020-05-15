package com.example.spotifypro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlay extends AppCompatActivity implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {
    Consumer cons = new Consumer("192.168.77.1", 5000);

    private ArrayList<MusicFile> listOfSongs = new ArrayList<MusicFile>();
    private ArrayList<MusicFile> listOfChunks = new ArrayList<>();
    private ArrayList<byte[]> listOfMusicExtraction = new ArrayList<>();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private MediaMetadataRetriever metadataRetriever=new MediaMetadataRetriever();


    private TextView songtitle, artist,currentTimer;
    private ImageView album;
    private ImageButton  searchImage,btnBack,btnForward,btnPlay,btnPause;
    SeekBar seekBar;
    byte[] mp3fileforplay;
    byte[] albumPic;
    private int songTime,realtime;
    private Button btnPrepare;
    final Handler handler=new Handler();


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
        btnPause=(ImageButton)findViewById(R.id.btnPause);
        album=(ImageView)findViewById(R.id.album);
        btnPrepare=(Button)findViewById(R.id.prepareSong);
        currentTimer=(TextView)findViewById(R.id.currentTimer);
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



        btnPrepare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyThirdTask third=new MyThirdTask();
                third.execute(mp3fileforplay);

                //αν καταργήσω το async task στο play?

            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //boolean isButtonPressed=false;
                if(!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                }
                updateSeekBar();
                btnPlay.setVisibility(View.INVISIBLE);
                btnPause.setVisibility(View.VISIBLE);
            }
        });
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);


        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }
                updateSeekBar();
                btnPrepare.setVisibility(View.INVISIBLE);
                btnPause.setVisibility(View.INVISIBLE);
                btnPlay.setVisibility(View.VISIBLE);
            }
        });


        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+7000);//duration of a chunk?
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-7000);//duration of a chunk?
            }
        });
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mediaPlayer.isPlaying()){
                    SeekBar seekbar=(SeekBar) v;
                    int playYou=(songTime/100)*seekbar.getProgress();
                    mediaPlayer.seekTo(playYou);

                }
                return false;
            }

        });


    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent);
        Log.e("WHAT IS GOING ON","HELP");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        btnPlay.setImageResource(R.drawable.play);
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
            btnPrepare.setVisibility(View.VISIBLE);
            searching.setVisibility(View.INVISIBLE);

        }

    }

    private class MyThirdTask extends AsyncTask<byte[],String,String>{

;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            seekBar.setVisibility(View.VISIBLE);
            btnForward.setVisibility(View.VISIBLE);
            btnForward.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.VISIBLE);
            songtitle.setVisibility(View.VISIBLE);
            artist.setVisibility(View.VISIBLE);
            currentTimer.setVisibility(View.VISIBLE);
            album.setVisibility(View.VISIBLE);
            searchImage.setVisibility(View.INVISIBLE);
            btnPause.setVisibility(View.VISIBLE);
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
            //super.onPostExecute(s);
            Log.d("ola kala:","paizeiii");
            songTime=mediaPlayer.getDuration();
            realtime=songTime;
            //boolean isButtonPressed=false;
            if(!mediaPlayer.isPlaying()){
                mediaPlayer.start();
            }else{
                mediaPlayer.pause();
            }
            updateSeekBar();

        }
    }


    private void updateSeekBar() {
        seekBar.setProgress((int)(((float)mediaPlayer.getCurrentPosition()/songTime)*100));
        if(mediaPlayer.isPlaying()){
            Runnable up=new Runnable(){

                @Override
                public void run() {
                    updateSeekBar();
                    realtime-=1000;//1 sec before
                    currentTimer.setText(String.format("%d:%d",TimeUnit.MILLISECONDS.toMinutes(songTime),TimeUnit.MILLISECONDS.toSeconds(songTime)));
                }
            };
            handler.postDelayed(up,1000);
        }
    }


    //play the song
    private MediaPlayer playMp3(byte[] mp3SoundByteArray,String title) throws IOException {
        File tempMp3file= File.createTempFile(title,"mp3",getCacheDir());
        tempMp3file.deleteOnExit();
        FileOutputStream f=new FileOutputStream(tempMp3file);
        f.write(mp3SoundByteArray);
        f.close();

        mediaPlayer.reset();
        MediaPlayer mediaPlayer = new MediaPlayer();
        MediaMetadataRetriever metadataRetriever=new MediaMetadataRetriever();
        FileInputStream fis = new FileInputStream(tempMp3file);
        mediaPlayer.setDataSource(fis.getFD());

        //ο κωδικας για δοκιμη του αλμπουμ
        /*try{
                metadataRetriever.setDataSource(fis.getFD());
                albumPic=metadataRetriever.getEmbeddedPicture();
                Bitmap songImage = BitmapFactory.decodeByteArray(albumPic, 0, albumPic.length);
                album.setImageBitmap(songImage);
        }catch(Exception e) {
                album.setImageResource(R.mipmap.appicontwo_background);
        }*/
        mediaPlayer.prepare();

        return mediaPlayer;

    }

}
