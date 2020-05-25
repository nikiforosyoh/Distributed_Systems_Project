package com.example.spotifypro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import java.util.ArrayList;

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

    }
}