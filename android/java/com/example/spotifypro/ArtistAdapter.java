package com.example.spotifypro;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.spotifypro.Glob.mediaPlayer;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    ArrayList<String> artist;
    Context context;

    public ArtistAdapter(Context ct,ArrayList<String> artistlist){
        context=ct;
        artist=artistlist;
    }

    @NonNull
    @Override
    public ArtistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View v=inflater.inflate(R.layout.list_items,parent,false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvName;
        ImageView musicians;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvName=itemView.findViewById(R.id.tvName);
            musicians=itemView.findViewById(R.id.musicians);

        }

        @Override
        public void onClick(View v) {
            /* if (mediaPlayer.isPlaying()){
                mediaPlayer.stop();
             }*/
            int pos=getAdapterPosition();
            Intent intent=new Intent(context,MusicPlay.class);
            intent.putExtra("ArtistName",tvName.getText().toString());
            context.startActivity(intent);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(artist.get(position));
    }

    @Override
    public int getItemCount() {
        return artist.size();
    }
}
