package com.sinhvien.appdean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.text.CaseMap;
import android.icu.text.StringSearch;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    public static final int REQUEST_CODE = 1; // Lấy kết quả trả về

    static ArrayList<MusicFiles> musicFiles;//List View
    static boolean shuffleBoolean = false, repeatBoolean = false;
    static  ArrayList<MusicFiles> albums = new ArrayList<>();
    static  ArrayList<MusicFiles> artists = new ArrayList<>();
    private String MY_SORT_PREF ="SortOrder";
    public  static final String MUSIC_LAST_PLAYED ="LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String ARTIST_NAME = "ARTIST NAME";
    public static final String SONG_NAME = "SONG NAME";
    public static boolean SHOW_MINI_PLAYER = false;
    public static String PATH_TO_FRAG = null;
    public static String ARTIST_TO_FRAG = null;
    public static String SONG_NAME_TO_FRAG = null;
    ImageView img_user;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(null);
        anhXaViewPager();
        permission();
        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        String email = intent.getStringExtra("EMAIL");
        // Hiển thị dữ liệu lên TextView
        textView = findViewById(R.id.tvTkhoanne);
        textView.setText(email);
        // Truy xuất giá trị của TextView từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String text = sharedPreferences.getString("EDIT_TEXT_VALUE", "");
        textView.setText(text);
    }

    //Cấp quyền ứng dụng
    private void permission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    , REQUEST_CODE);
        } else {
            musicFiles = getAllAudio(this);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                musicFiles = getAllAudio(this);
                anhXaViewPager();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , REQUEST_CODE);
            }
        }
    }
//viewPager
    private void anhXaViewPager() {
        ViewPager viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new SongsFragment(), "Bài Hát");
        viewPagerAdapter.addFragments(new AlbumFragment(), "Album");
        viewPagerAdapter.addFragments(new ArtitsFragment(),"Nghệ Sĩ");
        viewPagerAdapter.addFragments(new UsersFragment(),"Users");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    public static class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        void addFragments(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    // Lấy tất cả các tệp âm thanh có trong thiết bị của người dùng thông qua Content Provider
    public ArrayList<MusicFiles> getAllAudio(Context context)
    {
        // Lấy tất cả các tệp âm thanh có trong thiết bị của người dùng thông qua Content Provider
        SharedPreferences preferences = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE);
        String sortOrder = preferences.getString("sorting","sortByName");
        ArrayList<String> duplicate = new ArrayList<>();//lưu trữ danh sách album.
        albums.clear();
        artists.clear();
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>(); //lưu trữ danh sách tạm thời các file âm thanh
        String order = null;
        // Tạo một Uri để truy vấn tất cả các tệp âm thanh thông qua Content Provider.
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        switch (sortOrder)
        {
            case "sortByName":
                order =MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                break;
            case "sortByDate":
                order =MediaStore.MediaColumns.DATE_ADDED + " ASC";
                break;
            case "sortBySize":
                order =MediaStore.MediaColumns.SIZE + " DESC";
                break;
        }
        // Xác định các cột muốn truy vấn thông qua projection (ALBUM, TITLE, DURATION, DATA, ARTIST, _ID).
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID
        };
        // Thực hiện truy vấn thông qua cursor.
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, order);
        if (cursor !=null)
        {
            while (cursor.moveToNext())
            {
                // Trích xuất thông tin về album, tiêu đề, đường dẫn, nghệ sĩ và thời lượng của tệp âm thanh từ cursor.
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration= cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                String id = cursor.getString(5);
                // Thêm vào danh sách các tệp âm thanh tạm thời
                MusicFiles musicFiles=new MusicFiles(path, title, artist, album, duration, id);
                Log.e("Patch : " + path,"Album : " +album);
                tempAudioList.add(musicFiles);
                if (!duplicate.contains(album))
                {
                    albums.add(musicFiles);
                    duplicate.add(album);// Đã thêm vào danh sách

                }
                if (!duplicate.contains(artist))
                {
                    artists.add(musicFiles);
                    duplicate.add(artist);// Đã thêm vào danh sách

                }
            }
            cursor.close();
        }
        return tempAudioList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.search_option);
        SearchView searchView =(SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Tìm kiếm bài hát");
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onQueryTextSubmit(String query)           {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput = normalizeString(newText.toLowerCase());
        ArrayList<MusicFiles> myFiles = new ArrayList<>();
        for(MusicFiles song : musicFiles)
        {
            if (normalizeString(song.getTitle().toLowerCase()).contains(userInput))
            {
                myFiles.add(song);
            }
        }
        SongsFragment.musicAdapter.updatelist(myFiles);
        return true;
    }
//  Sử dụng bộ lọc của thư viện java.text.Normalizer để loại bỏ các dấu trong tiếng Việt
    private String normalizeString(String input) {
        String regex = "\\p{InCombiningDiacriticalMarks}+";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll(regex, "");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor = getSharedPreferences(MY_SORT_PREF,MODE_PRIVATE).edit();
        switch (item.getItemId())
        {
             case R.id.by_name:
                 editor.putString("sorting","sortByName");
                 editor.apply();
                 this.recreate();
                 break;
            case R.id.by_date:
                editor.putString("sorting","sortByDate");
                editor.apply();
                this.recreate();
                break;
            case R.id.by_size:
                editor.putString("sorting","sortBySize");
                editor.apply();
                this.recreate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        super.onResume();
        SharedPreferences preferences = getSharedPreferences(MUSIC_LAST_PLAYED,MODE_PRIVATE);
        String path= preferences.getString(MUSIC_FILE, null);
        String artist = preferences.getString(ARTIST_NAME,null);
        String song_name = preferences.getString(SONG_NAME,null);
        if (path != null) {
            SHOW_MINI_PLAYER = true;
            PATH_TO_FRAG = path;
            ARTIST_TO_FRAG = artist;
            SONG_NAME_TO_FRAG = song_name;
        }
        else {
            SHOW_MINI_PLAYER = false;
            PATH_TO_FRAG = null;
            ARTIST_TO_FRAG = null;
            SONG_NAME_TO_FRAG = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Lưu giá trị của TextView vào SharedPreferences
        String text = textView.getText().toString();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("TEXT_VIEW_VALUE", text);
        editor.apply();
    }
}



