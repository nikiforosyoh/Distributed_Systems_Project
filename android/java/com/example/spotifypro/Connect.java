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


//the second activity->instructions:
/*The first time the user opens the app ,he must write the artist name that he wants and click the button next
* The other times the user can 1.write the artist he wants and click the button next
*                              2.click the playlist button in order to find a list that includes all the artists*/
public class Connect extends AppCompatActivity {

    //declare the basic elements
    Button NextButton;
    ImageButton playlist;

    private static ArrayList<String> ListOfArtists1=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        //connect the element with suitable ids
        NextButton = (Button) findViewById(R.id.NextButton);
        final EditText ArtistName = (EditText) findViewById(R.id.ArtistName);


        //when i click this button,i transfer the value of artist name to the next activity
        NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String requestArtist = ArtistName.getText().toString().trim();
                Intent intent = new Intent(Connect.this, MusicPlay.class);
                intent.putExtra("ARTIST", requestArtist);
                startActivity(intent);

            }
        });

        //when i click this button ,i transfer the arraylist of artists to the ArtistList activity !!!!!!!!!! ATTENTION:This button muste be used after the first time,otherwise it's empty ->error?
        /*
        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playintent=new Intent(Connect.this,ArtistList.class);
                ListOfArtists1=(ArrayList<String>)getIntent().getSerializableExtra("ListOfArtists");
                playintent.putExtra("ListOfArtists",ListOfArtists1);
                startActivity(playintent);
            }
        });
        */


    }

}

