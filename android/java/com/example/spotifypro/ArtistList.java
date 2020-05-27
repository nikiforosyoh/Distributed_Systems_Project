package com.example.spotifypro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import static com.example.spotifypro.Glob.cons;
import static com.example.spotifypro.Glob.firstTime;
import static com.example.spotifypro.Glob.songFound;

public class ArtistList extends AppCompatActivity {
    RecyclerView recyclerview;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;

    private static ArrayList<String> art=new ArrayList<>();
    private static HashMap<String,Info> brokerArt=new HashMap<String,Info>();
    boolean listFull=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_list);
        //ListOfArtists=(ArrayList<String>)getIntent().getSerializableExtra("ListOfArtists");
        if(!songFound){
            displayPopup(findViewById(R.id.rootLayout));
            songFound=true;
        }
        if(firstTime) {
            ArtistList.MyFirstTask first = new ArtistList.MyFirstTask();
            first.execute();
            while (!listFull) ;
            firstTime = false;
        }
        //registerbtn.setVisibility(View.INVISIBLE);
        recyclerview=(RecyclerView)findViewById(R.id.myList);
        recyclerview.setHasFixedSize(true);

        layoutManager=new LinearLayoutManager(this);
        recyclerview.setLayoutManager((layoutManager));

        myAdapter=new ArtistAdapter(this,art);
        recyclerview.setAdapter(myAdapter);
    }
    
    public void displayPopup(View v){
        Snackbar snackbar = Snackbar.make(findViewById(R.id.rootLayout),R.string.No_such_song,Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(getColor(R.color.colorPrimaryDark));
        snackbar.show();
    }

    private class MyFirstTask extends AsyncTask<String, Consumer, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            Log.d("....", "Initialization");
        }

        @Override
        protected ArrayList<String> doInBackground(String... arg0) {
            cons = new Consumer("192.168.1.8", 5000);
            Log.d("Insert", "First stage");
            cons.init(cons.getBrokerIp(), cons.getBrokerPort(), cons.getAvailableBrokers());
            Log.d("myTag3", "Problem2");
            brokerArt = cons.initialization();
            art = createArtList(brokerArt);
            listFull=true;
            Log.d("myTag4", "Problem3");
            return art;
        }
    }

    private ArrayList<String> createArtList(HashMap<String, Info> brokerArt) {
        for (String artist : brokerArt.keySet()) {
            art.add(artist);
            Log.d("papapa: ", artist);
        }
        return art;
    }
}
