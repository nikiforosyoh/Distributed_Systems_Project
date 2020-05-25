package com.example.spotifypro;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import static com.example.spotifypro.Node.getN;
import static com.example.spotifypro.Glob.cons;

//Third activity->instructions:
/*
    1.enter the song of the artist you chose previously
    2.Click the button "Search"
    3.Click the button PLAY and the song begins....
*/
public class MusicPlay extends AppCompatActivity implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {

    private ArrayList<MusicFile> listOfSongs = new ArrayList<MusicFile>();
    private ArrayList<MusicFile> listOfChunks = new ArrayList<>();
    private ArrayList<byte[]> listOfMusicExtraction = new ArrayList<>();
    private MediaPlayer mediaPlayer = new MediaPlayer();

    //declare the basic elements
    private TextView songtitle, artist,currentTimer;
    private ImageView album;
    private ImageButton  searchImage,btnBack,btnForward,btnPlay,btnPause,artistListbtn;
    SeekBar seekBar;
    String timer;
    byte[] mp3fileforplay;
    byte[] albumPic;
    private int songTime;
    private Button btnPrepare;
    final Handler handler=new Handler();
    int minutes;
    int seconds;
    int time;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        //connect the element with suitable ids
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
        artistListbtn=(ImageButton)findViewById(R.id.backActivity);
        final EditText SongTitle = (EditText) findViewById(R.id.SongTitle);

        //take the values from the Connect activity
        artist.setText(getIntent().getStringExtra("ARTIST"));

        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SongTitle.getText().toString().isEmpty()) {
                    Toast.makeText(MusicPlay.this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Check the control", "Done");
                }

                artist.setText(getIntent().getStringExtra("ArtistName"));
                String requestArtist = artist.getText().toString().trim();
                String requestSong = SongTitle.getText().toString().trim();
                MySecondTask second = new MySecondTask();
                second.execute(requestArtist, requestSong);
            }
        });

        btnPrepare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyThirdTask third=new MyThirdTask();
                third.execute(mp3fileforplay);
                btnPrepare.setVisibility(View.INVISIBLE);
                artistListbtn.setVisibility(View.VISIBLE);
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

        artistListbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent artistlistintent=new Intent(MusicPlay.this,ArtistList.class);
                startActivity(artistlistintent);
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent artistlistintent=new Intent(MusicPlay.this,ArtistList.class);
        startActivity(artistlistintent);
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
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
            searching.setText("Please Wait....");
        }

        //@RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected byte[] doInBackground(String... arg0) {
            String requestArt = arg0[0];
            String requestS = arg0[1];

            try {
                listOfChunks=cons.openConsumer(requestArt,requestS,cons.getBrokerArtist());

                //in case a song that doesn't exist
                if (listOfChunks.isEmpty()){

                    Intent artistlistintent=new Intent(MusicPlay.this,ArtistList.class);
                    startActivity(artistlistintent);
                    return null;
                }

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
            songtitle.setText(listOfChunks.get(0).trackName.substring(0,1).toUpperCase()+listOfChunks.get(0).trackName.substring(1));

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
        int value = seekBar.getProgress();


        if(mediaPlayer.isPlaying()){
            Runnable up=new Runnable(){

                @Override
                public void run() {
                    updateSeekBar();
                    //currentTimer.setText(String.format("%d:%d",(songTime/1000)/60,(songTime/1000)%60);

                    //Timer
                    if (value%60 <= 9){
                        timer = (value / 60) + ":0" + (value % 60);
                    }else {
                        timer = (value / 60) + ":" + (value % 60);
                    }

                    //Countdown
                    /*
                    time = songTime/1000 - value;
                    Log.d("time", String.valueOf(time));
                    minutes = time / 60;
                    seconds = time % 60;

                    if (seconds <= 9){
                        timer = minutes + ":0" + seconds;
                    }else {
                        timer = minutes + ":" + seconds;
                    }
                    */
                    currentTimer.setText(timer);
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

        try{
            metadataRetriever.setDataSource(fis.getFD());
            albumPic=metadataRetriever.getEmbeddedPicture();
            Bitmap songImage = BitmapFactory.decodeByteArray(albumPic, 0, albumPic.length);
            album.setImageBitmap(songImage);
        }catch(Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.prepare();

        return mediaPlayer;
    }
}