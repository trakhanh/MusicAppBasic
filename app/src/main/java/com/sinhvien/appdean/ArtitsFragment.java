package com.sinhvien.appdean;

import static com.sinhvien.appdean.MainActivity.albums;
import static com.sinhvien.appdean.MainActivity.artists;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ArtitsFragment extends Fragment {
    RecyclerView recyclerView;
    ArtistAdapter artistAdapter;


    public ArtitsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artits, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        if(!(artists.size() <1))
        {
            artistAdapter = new ArtistAdapter(getContext(),artists);
            recyclerView.setAdapter(artistAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        }
        return view;
    }
}