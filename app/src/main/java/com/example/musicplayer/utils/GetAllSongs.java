package com.example.musicplayer.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import com.example.musicplayer.R;
import com.example.musicplayer.data.SongData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class GetAllSongs {
    private static final String TAG = GetAllSongs.class.getSimpleName();
    private static final String IMAGE_NOT_AVAILABLE = "Image not available";

    private ArrayList<SongData> songs = new ArrayList<>();

    public ArrayList getAllTracks(Context context) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String source = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String albumArt;
                    try {
                        albumArt = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART));
                    } catch (IllegalArgumentException e) {
                        Log.e(TAG, "getAllTracks: Column does not exists");
                        albumArt = IMAGE_NOT_AVAILABLE;
                    }

                   /* //  getting album art
                    Long albumId = cursor.getLong(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    Uri sArtworkUri = Uri
                            .parse("content://media/external/audio/albumart");
                    Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);

                    Log.d(TAG, "getAllTracks: album art uri" + albumArtUri);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(
                                context.getContentResolver(), albumArtUri);
                        bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);

                    } catch (FileNotFoundException exception) {
                        Log.e(TAG, "getAllTracks: ", exception);
                        bitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.spinner_animation);
                    } catch (IOException e) {
                        Log.e(TAG, "getAllTracks: ", e);
                    }*/

                    Log.d(TAG, "getAllTracks: bitmap: " + albumArt);

                    //  adding the data
//                    OfflineSongData s = new OfflineSongData(title, artist,source);
//                    songs.add(s);

                } while (cursor.moveToNext());
            }

            cursor.close();
            return songs;
        }

        return null;
    }

    public ArrayList<SongData> initLayout(Context context) throws NullPointerException {

        ArrayList<SongData> offlineSongs = new ArrayList<>();

        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION};
        final String where = MediaStore.Audio.Media.IS_MUSIC + "=1";
        final Cursor cursor = context.getContentResolver().query(uri,
                cursor_cols, where, null, null);

        while (cursor.moveToNext()) {
            String artist = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
            String track = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            String data = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            long albumId = cursor.getLong(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

            int duration = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

            Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);

            Log.d(TAG, "initLayout: album art uri: " + albumArtUri.toString());
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                        context.getContentResolver(), albumArtUri);
                bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);

            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.spinner_animation);
            } catch (IOException e) {

                e.printStackTrace();
            }
            if (duration > 20)  //  if duration is more than 20 sec then only add the song
                offlineSongs.add(new SongData(track, artist, bitmap, data));
        }

//        Log.d(TAG, "initLayout: offline songs ==> $offlineSongs");
        return offlineSongs;
    }
}
