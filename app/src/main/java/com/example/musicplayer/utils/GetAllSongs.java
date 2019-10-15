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

    private Context context;
    private ArrayList<SongData> offlineSongs = new ArrayList<>();
    private ArrayList<String> offlineSongsTitle = new ArrayList<>();

    public GetAllSongs(Context context) {
        this.context = context;
    }

    public int indexOfSongFromTitle(String title) {
        int indexOfTitle = offlineSongsTitle.indexOf(title);
        Log.d(TAG, "indexOfSongFromTitle: index of title ==> " + indexOfTitle);
        return indexOfTitle;
    }

    public ArrayList<String> getAllSongsTitle() {
        return offlineSongsTitle;
    }

    public String titleFromIndex(int index) {
        return getOfflineTracks().get(index).component1();
    }

    public ArrayList<SongData> getOfflineTracks() throws NullPointerException {

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
            String title = cursor.getString(cursor
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

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                        context.getContentResolver(), albumArtUri);
                bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);

            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.record_image_50_50);
            } catch (IOException e) {

                e.printStackTrace();
            }
            if (duration > 20 * 1000)  //  if duration is more than 20 sec then only add the song
            {
                Log.d(TAG, "getOfflineTracks: title ==> " + title + "artist ==> " + artist + "image ==> " + bitmap
                        + "source ==> " + data
                );

                offlineSongs.add(new SongData(title, artist, bitmap, data));
                offlineSongsTitle.add(title);
            }
        }

//        Log.d(TAG, "getOfflineTracks: offline songs ==> $offlineSongs");

        return offlineSongs;
    }

}
