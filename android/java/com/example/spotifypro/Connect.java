package com.example.spotifypro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Connect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        Button NextButton = (Button) findViewById(R.id.NextButton);
        //Button searchButton=(Button) findViewById(R.id.searchButton);
        ImageButton searchImage = (ImageButton) findViewById(R.id.searchImage);
        final EditText ArtistName = (EditText) findViewById(R.id.ArtistName);
        //final EditText SongTitle=(EditText)findViewById(R.id.SongTitle);


        NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String requestArtist = ArtistName.getText().toString().trim();
                Intent intent = new Intent(Connect.this, MusicPlay.class);
                //intent.putExtra("SONG",mp3File);
                //intent.putExtras(bundle);
                intent.putExtra("ARTIST", requestArtist);
                startActivity(intent);

            }
        });


    }



}

