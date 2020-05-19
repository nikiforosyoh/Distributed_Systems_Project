package com.example.spotifypro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class ArtistList extends AppCompatActivity {
    RecyclerView recyclerview;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;
    private static ArrayList<String> ListOfArtists=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_list);
        ListOfArtists=(ArrayList<String>)getIntent().getSerializableExtra("ListOfArtists");
        recyclerview=(RecyclerView)findViewById(R.id.myList);
        recyclerview.setHasFixedSize(true);


        layoutManager=new LinearLayoutManager(this);
        recyclerview.setLayoutManager((layoutManager));



        myAdapter=new ArtistAdapter(this,ListOfArtists);
        recyclerview.setAdapter(myAdapter);






    }
}
