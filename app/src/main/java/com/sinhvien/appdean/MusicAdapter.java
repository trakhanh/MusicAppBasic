package com.sinhvien.appdean;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyVieHolder>{
    private Context mContext;
    static ArrayList<MusicFiles> mFiles;


    MusicAdapter(Context mContext, ArrayList<MusicFiles> mFiles) {
        this.mFiles = mFiles;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    // tạo một ViewHolder mới để hiển thị các item trong RecyclerView, sử dụng layout music_items.
    public MyVieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items,parent,false);
        return new MyVieHolder(view);
    }

    @Override
    //gán các giá trị của các MusicFiles vào ViewHolder để hiển thị trong RecyclerView
    public void onBindViewHolder(@NonNull MyVieHolder holder, int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());
        holder.artis_name.setText(mFiles.get(position).getArtist());
        byte[] image  = getAlbumArt(mFiles.get(position).getPath());
        if  (image != null)
        {
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.album_art);
        }
        else{
            Glide.with(mContext)
                    .load(R.drawable.hinhne)
                    .into(holder.album_art);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("position",holder.getAbsoluteAdapterPosition());
                mContext.startActivity(intent);
            }
        });
        holder.menuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.delete:
                                Toast.makeText(mContext, "Xoá thành công", Toast.LENGTH_SHORT).show();
                                deleteFile(position, v);
                                break;
                        }
                        return true;
                    }
                });
            }
        });
    }
    private void  deleteFile(int position, View v)
    {
        //Tạo đường dẫn uri
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(mFiles.get(position).getId())); //content://
        File file = new File(mFiles.get(position).getPath());
        boolean deleted = file.delete(); //xoa file
        if (deleted) {
            mContext.getContentResolver().delete(contentUri,null,null);
            mFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mFiles.size());
            Snackbar.make(v, "File đã bị xoá", Snackbar.LENGTH_LONG).show();
        }
        else {
            Snackbar.make(v, "Không thể xoá file:", Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    public int getItemCount()  {
        return mFiles.size();
    }

    public static class MyVieHolder extends RecyclerView.ViewHolder
    {

        TextView file_name,artis_name;
        ImageView album_art, menuMore;
        public MyVieHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
            album_art = itemView.findViewById(R.id.music_img);
            menuMore = itemView.findViewById(R.id.menuMore);
            artis_name = itemView.findViewById(R.id.music_artis_name);
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
    void updatelist(ArrayList<MusicFiles> musicFilesArrayList)
    {
        mFiles = new ArrayList<>();
        mFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }
}
