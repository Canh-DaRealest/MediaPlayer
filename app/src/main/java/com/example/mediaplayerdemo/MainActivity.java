package com.example.mediaplayerdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;

import com.example.mediaplayerdemo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
				private ActivityMainBinding binding;


				@Override
				protected void onCreate(Bundle savedInstanceState) {
								super.onCreate(savedInstanceState);
								binding = ActivityMainBinding.inflate(getLayoutInflater());

								setContentView(binding.getRoot());
								initView();
				}

				private void initView() {
								binding.ivBgSound.setOnClickListener(v -> playBGSound());
								binding.btStopBgSound.setOnClickListener(v -> stopBgSound());
								binding.ivGameSound.setOnClickListener(v -> playGameSong());
				}

				private void playGameSong() {
								MediaManager.getInstance().playSong(R.raw.best_player, mp -> {
												MediaManager.getInstance().playSong(R.raw.bgmusic);
								});
				}

				private void stopBgSound() {
								MediaManager.getInstance().stopBG();
				}

				private void playBGSound() {
								MediaManager.getInstance().playBg(R.raw.background_music, true);

				}

				@Override
				protected void onStop() {
								MediaManager.getInstance().pauseBG();

								super.onStop();
				}

				@Override
				protected void onStart() {
								MediaManager.getInstance().playBG();
								super.onStart();
				}
}
