package com.example.mediaplayerdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediaplayerdemo.databinding.M002ActivityMainBinding;

import java.util.Objects;

public class M002Activity extends AppCompatActivity implements View.OnClickListener {
    private M002ActivityMainBinding binding;

    private static final int LEVEL_PLAY = 1;
    private static final int LEVEL_PAUSE = 0;
    private SongAdapter songAdapter;
    private boolean appRunning = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        binding = M002ActivityMainBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);

        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {

        checkUserPermisson();

    }

    private void checkUserPermisson() {
        int readExternalStoragePermisson = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

        if (readExternalStoragePermisson != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);

        } else {
            Mp3Player.getInstance().loadOffline();
            Mp3Player.getInstance().setOnCompletionListenerCallBack(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    updateUI();
                }
            });
            initListSong();
        }
    }

    private void initListSong() {
        songAdapter = new SongAdapter(Mp3Player.getInstance().getSongList(), this);

        binding.rvRecycleview.setAdapter(songAdapter);
        binding.includeItemControl.seekbar.setOnSeekBarChangeListener(new OnSeekBarChange() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Mp3Player.getInstance().seekTo(seekBar.getProgress());
            }
        });
        binding.rvRecycleview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.rvRecycleview.addItemDecoration(itemDecoration);
        songAdapter.getSongLiveData().observe(this, new Observer<Song>() {
            @Override
            public void onChanged(Song song) {
                Mp3Player.getInstance().play(song);
                updateUI();
            }
        });
        binding.includeItemControl.ivNext.setOnClickListener(this);
        binding.includeItemControl.ivPrevious.setOnClickListener(this);
        binding.includeItemControl.ivPlayPause.setOnClickListener(this);
        appRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateSeekbar();
            }
        }).start();
        updateUI();
    }


    private void updateSeekbar() {
        while (appRunning) {
            try {
                Thread.sleep(500);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String currentTimeText = Mp3Player.getInstance().getCurrentTimeText();
                        String totalTimeText = Mp3Player.getInstance().getTotalTimeText();
                        int currentTime = Mp3Player.getInstance().getCurrentTime();
                        int totalTime = Mp3Player.getInstance().getTotalTime();

                        binding.includeItemControl.seekbar.setMax(totalTime);
                        binding.includeItemControl.seekbar.setProgress(currentTime);
                        binding.includeItemControl.tvDuration.setText(String.format("%s/%s", currentTimeText, totalTimeText));
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appRunning = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Mp3Player.getInstance().loadOffline();
                initListSong();
            } else {
                Toast.makeText(this, "vui long chap nhan quyen de tiep tuc tac vu", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_play_pause) {
            Mp3Player.getInstance().play();
        } else if (v.getId() == R.id.iv_next) {
            Mp3Player.getInstance().next();
        } else {
            Mp3Player.getInstance().back();
        }
        updateUI();

    }

    private void updateUI() {
        if (Mp3Player.getInstance().getState() == Mp3Player.STATE_PLAYING) {
            binding.includeItemControl.ivPlayPause.setImageLevel(LEVEL_PLAY);
        } else {
            binding.includeItemControl.ivPlayPause.setImageLevel(LEVEL_PAUSE);
        }
        Song song = Mp3Player.getInstance().getCurrentSong();

        binding.includeItemControl.tvName.setText(song.title);
        binding.includeItemControl.tvAlbum.setText(song.album);

        ((SongAdapter) Objects.requireNonNull(binding.rvRecycleview.getAdapter())).updateUI(Mp3Player.getInstance().getCurrentIndex());
    }

}
