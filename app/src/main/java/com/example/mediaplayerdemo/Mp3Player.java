package com.example.mediaplayerdemo;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Mp3Player {

    public static final int STATE_IDLE = 1;
    public static final int STATE_PLAYING = 2;
    public static final int STATE_PAUSED = 3;
    private static int state = STATE_IDLE;
    private final MediaPlayer player;
    public MediaPlayer.OnCompletionListener onCompletionListenerCallBack;


    private List<Song> songList = new ArrayList<>();

    public int getState() {
        return state;
    }

    private static Mp3Player instance;

    public Mp3Player() {
        player = new MediaPlayer();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                next();
                onCompletionListenerCallBack.onCompletion(null);
            }
        });
        player.setAudioAttributes(new AudioAttributes.Builder()
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build());
    }

    public void setOnCompletionListenerCallBack(MediaPlayer.OnCompletionListener onCompletionListenerCallBack) {
        this.onCompletionListenerCallBack = onCompletionListenerCallBack;

    }

    public List<Song> getSongList() {
        return songList;
    }

    public static Mp3Player getInstance() {
        if (instance == null) {
            instance = new Mp3Player();
        }
        return instance;
    }

    public void loadOffline() {
        Cursor c = App.getInstance().getContentResolver().
                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                        null, null, MediaStore.Audio.Media.TITLE + " ASC");

        c.moveToFirst();
        int cTitle = c.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int cPath = c.getColumnIndex(MediaStore.Audio.Media.DATA);
        int cAlbum = c.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        int cArtist = c.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        songList.clear();
        while (!c.isAfterLast()) {
            String title = c.getString(cTitle);
            String path = c.getString(cPath);
            String album = c.getString(cAlbum);
            String artist = c.getString(cArtist);

            Uri uri = getArtUriFromMusicFile(App.getInstance(), new File(path));
            Song song = new Song(title, path, album, artist, uri);
            songList.add(song);
            c.moveToNext();
        }
        c.close();
        Log.i(TAG, "loadOffline: " + songList.size());
    }

    private int currentSong;

    public void play() {
        if (state == STATE_IDLE) {
            player.reset();
            try {
                player.setDataSource(songList.get(currentSong).path);
                player.prepare();
                player.start();
                state = STATE_PLAYING;
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (state == STATE_PAUSED) {
            player.start();
            state = STATE_PLAYING;

        } else {
            player.pause();
            state = STATE_PAUSED;

        }
    }

    public void next() {

        if (currentSong == songList.size() - 1) {
            currentSong = 0;

        } else {
            currentSong++;
        }
        state = STATE_IDLE;
        play();
    }

    public void back() {

        if (currentSong == 0) {
            currentSong = songList.size() - 1;

        } else {
            currentSong--;
        }

        state = STATE_IDLE;
        play();
    }

    public Song getCurrentSong() {
        return songList.get(currentSong);
    }

    public void play(Song song) {
        currentSong = songList.indexOf(song);
        state = STATE_IDLE;
        play();
    }

    public static Uri getArtUriFromMusicFile(Context context, File file) {
        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = {MediaStore.Audio.Media.ALBUM_ID};

        final String where = MediaStore.Audio.Media.IS_MUSIC + "=1 AND " + MediaStore.Audio.Media.DATA + " = '"
                + file.getAbsolutePath() + "'";
        final Cursor cursor = context.getApplicationContext().getContentResolver().query(uri, cursor_cols, where, null, null);
        /*
         * If the cusor count is greater than 0 then parse the data and get the art id.
         */
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
            cursor.close();
            return albumArtUri;
        }
        return Uri.EMPTY;
    }

    public int getCurrentIndex() {
        return currentSong;
    }

    public String getCurrentTimeText() {
        try {
            SimpleDateFormat df = new SimpleDateFormat("mm:ss");
            return df.format(new Date(player.getCurrentPosition()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "--";
    }

    public String getTotalTimeText() {
        try {
            SimpleDateFormat df = new SimpleDateFormat("mm:ss");
            return df.format(new Date(player.getDuration()));
        } catch (Exception e) {

        }
        return "--";
    }

    public int getTotalTime() {
        return player.getDuration();
    }

    public int getCurrentTime() {
        return player.getCurrentPosition();
    }

    public void seekTo(int progress) {
        try {
            player.seekTo(progress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
