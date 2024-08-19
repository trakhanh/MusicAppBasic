package com.sinhvien.appdean;

import static com.sinhvien.appdean.MainActivity.musicFiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

public class ArtistDetails extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView artistPhoto,back_btn;
    String artistName;
    ArrayList<MusicFiles> artistSongs = new ArrayList<>();
    ArtistDetailsAdapter artistDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_details);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
        artistPhoto = (ImageView) findViewById(R.id.artistPhoto);
        artistName = getIntent().getStringExtra("artistName");
        int j =0;
        for(int i = 0 ; i < musicFiles.size(); i++)
        {
            if(artistName.equals(musicFiles.get(i).getAlbum()))
            {
                artistSongs.add(j, musicFiles.get(i));
                j++;
            }
        }
        byte [] image = getAlbumArt(artistSongs.get(0).getPath());
        if(image !=null)
        {
            Glide.with(this)
                    .load(image)
                    .into(artistPhoto);
        }
        else
        {
            Glide.with(this)
                    .load(R.drawable.hinhne)
                    .into(artistPhoto);
        }
        back_btn = (ImageView) findViewById(R.id.back2_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArtistDetails.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(!(artistSongs.size() < 1))
        {
            artistDetailsAdapter = new ArtistDetailsAdapter(this,artistSongs);
            recyclerView.setAdapter(artistDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,
                    RecyclerView.VERTICAL,false));
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
