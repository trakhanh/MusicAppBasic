package com.sinhvien.appdean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "UserDbHelper";
    public static final String  DATABASE_NAME = "SignLog.db";
    private static final String TABLE_USER = "users";
    private static final String TABLE_PLAYLISTS ="playlists";
    private static final String TABLE_ARTISTS = "artists";
    private static final String TABLE_ALBUM = "album";
    private static final String TABLE_SONGS = "songs";

    public DatabaseHelper(@Nullable Context context) {
        super(context,DATABASE_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase MyDatabase) {
        Log.i(TAG, "Create table user");
        String queryCreateTable = "CREATE TABLE " + TABLE_USER + " ( " +
                "email TEXT PRIMARY KEY, " +
                "password TEXT " +
                ")";
        Log.i(TAG, "Create table playlist");
        String queryCreateTable1 = "CREATE TABLE " + TABLE_PLAYLISTS + " ( " +
                "playlists_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "playlists_name TEXT "+
                ")";
        Log.i(TAG,"create table artists");
        String queryCreateTable2 = "CREATE TABLE " + TABLE_ARTISTS + " ( " +
                "artist_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "artist_name TEXT " +
                ")";
        Log.i(TAG,"create table album");
        String queryCreateTable3 = "CREATE TABLE " + TABLE_ALBUM + "(" +
                "album_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "album_name TEXT, "+
                "artists_id INTEGER, " +
                "release_date INTEGER, " +
                "album_cover_url TEXT " +
                ")";
        Log.i(TAG, "create table song");
        String queryCreateTable4 = "CREATE TABLE " + TABLE_SONGS + "(" +
                " song_id INTEGER PRIMARY KEY AUTOINCREMENT, "  +
                "song_name TEXT, " +
                "album_id INTEGER, " +
                "track_number INTEGER " +
                ")";
        MyDatabase.execSQL(queryCreateTable);
        MyDatabase.execSQL(queryCreateTable1);
        MyDatabase.execSQL(queryCreateTable2);
        MyDatabase.execSQL(queryCreateTable3);
        MyDatabase.execSQL(queryCreateTable4);
    }
    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(MyDB);

    }
    ///// MyDatabase
    public Boolean insertData(String email, String password){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);
        long result = MyDatabase.insert("users", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public Boolean checkEmail(String email){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        if(cursor.getCount() > 0) {
            return true;
        }else {
            return false;
        }
    }
    public Boolean checkEmailPassword(String email, String password){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});
        if (cursor.getCount() > 0) {
            return true;
        }else {
            return false;
        }
    }
    public Boolean updatepassword(String email, String password) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("password", password);
        long result = MyDatabase.update("users",contentValues, "email = ?", new String[]{email});
        if (result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }

    }
    public Cursor getDataByEmail(String email){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("SELECT email, password FROM users WHERE email=?", new String[]{email});
        return cursor;
    }

}