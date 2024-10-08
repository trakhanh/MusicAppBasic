package com.sinhvien.appdean;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

public class ArtistDetailsAdapter extends RecyclerView.Adapter<ArtistDetailsAdapter.MyVHolder> {
    private Context mContext;
    static ArrayList<MusicFiles> artistFiles;
    View view;

    public ArtistDetailsAdapter(Context mContext, ArrayList<MusicFiles> artistFiles) {
        this.mContext = mContext;
        this.artistFiles = artistFiles;
    }

    @NonNull
    @Override
    public ArtistDetailsAdapter.MyVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.music_items,parent,false);
        return new ArtistDetailsAdapter.MyVHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistDetailsAdapter.MyVHolder holder, int position) {
        holder.album_name.setText(artistFiles.get(position).getTitle());
        holder.artist_name.setText(artistFiles.get(position).getArtist());
        byte[] image  = getAlbumArt(artistFiles.get(position).getPath());
        if  (image != null)
        {
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.album_image);
        }
        else{
            Glide.with(mContext)
                    .load(R.drawable.hinhne)
                    .into(holder.album_image);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,PlayerActivity.class);
                intent.putExtra("sender","artistDetails");
                intent.putExtra("position",position);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return artistFiles.size();
    }

    public class MyVHolder extends RecyclerView.ViewHolder {
        ImageView album_image;
        TextView album_name,artist_name;
        public MyVHolder(@NonNull View itemView) {
            super(itemView);
            album_image = itemView.findViewById(R.id.music_img);
            album_name = itemView.findViewById(R.id.music_file_name);
            artist_name = itemView.findViewById(R.id.music_artis_name);
        }
    }
    private byte [] getAlbumArt(String uri ){
        MediaMetadataRetriever retriever =  new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        try {
            retriever.release();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return art;
    }
}
