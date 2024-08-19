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

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.MyViHolder> {
    private Context mContext;
    private ArrayList<MusicFiles> artistFiles;
    View view;

    public ArtistAdapter(Context mContext, ArrayList<MusicFiles> artistFiles) {
        this.mContext = mContext;
        this.artistFiles = artistFiles;
    }
    @NonNull
    @Override
    public MyViHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.artist_item,parent,false);
        return new MyViHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistAdapter.MyViHolder holder, int position) {
        holder.artist_name.setText(artistFiles.get(position).getArtist());//đặt văn bản của TextView artist_name trong ViewHolder bằng tên album tương ứng trong danh sách.
        byte[] image  = getAlbumArt(artistFiles.get(position).getPath());
        if  (image != null)
        {
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.artist_image);
        }
        else{
            Glide.with(mContext)
                    .load(R.drawable.hinhne)
                    .into(holder.artist_image);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ArtistDetails.class);
                intent.putExtra("artistName", artistFiles.get(position).getAlbum());
                mContext.startActivity(intent);
            }
        });

    }
    public class MyViHolder extends RecyclerView.ViewHolder {
        ImageView artist_image ;
        TextView artist_name;
        public MyViHolder(@NonNull View itemView) {
            super(itemView);
            artist_image = itemView.findViewById(R.id.artist_image);
            artist_name = itemView.findViewById(R.id.artist_name);
        }
    }

    @Override
    public int getItemCount() {
        return artistFiles.size();
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
