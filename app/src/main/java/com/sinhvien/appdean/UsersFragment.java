package com.sinhvien.appdean;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.getIntent;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class UsersFragment extends Fragment {
    MediaPlayer mediaPlayer;
    TextView tvTkhoan,tvMkhau;
    private DatabaseHelper db;


    public UsersFragment() {
        // Required empty public constructor
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        Button btn_logout = view.findViewById(R.id.btn_logout);
        ImageView img_rotate = view.findViewById(R.id.imageViewz);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                Toast.makeText(getActivity(), "Đăng Xuất thành công", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                Intent intent1 = new Intent(getActivity(), MusicService.class);
                getActivity().stopService(intent1);
                onDestroy();
            }
        });
        final int[] angle = {0};
        img_rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                angle[0] = angle[0] + 90;
                img_rotate.setRotation(angle[0]);

            }
        });
        return view;
    }
}