package com.example.mediaplayerdemo;

import android.media.MediaPlayer;

public class MediaManager {

				private static MediaManager instance;
				private MediaPlayer bgPlayer;
				private MediaPlayer songPlayer;


				public static MediaManager getInstance() {
								if (instance == null) {
												instance = new MediaManager();
								}
								return instance;
				}


				public void playBg(int song, boolean isLooping) {

								if (this.bgPlayer != null) {
												this.bgPlayer.reset();
								}

								bgPlayer = play(song, isLooping);
				}
//nap chong
				public void playSong(int song) {

								playSong(song, false, null);

				}

				public void playSong(int song, MediaPlayer.OnCompletionListener event) {

								playSong(song, false, event);

				}

				public void playSong(int song, boolean isLooping, MediaPlayer.OnCompletionListener event) {

								if (this.songPlayer != null) {
												this.songPlayer.reset();
								}
								songPlayer = play(song, isLooping);
								songPlayer.setOnCompletionListener(event);
				}
//

				public MediaPlayer play(int song, boolean isLooping) {
								MediaPlayer player = MediaPlayer.create(App.getInstance(), song);
								player.setLooping(isLooping);
								player.start();

								return player;

				}

				public void pauseBG() {
								pause(bgPlayer);
				}

				public void pauseSong() {
								pause(songPlayer);
				}

				private void pause(MediaPlayer player) {
								if (player != null && player.isPlaying()) {
												player.pause();
								}
				}

				public void stopBG() {
								bgPlayer = stop(bgPlayer);

				}


				public void stopSong() {
								songPlayer = stop(songPlayer);

				}

				private MediaPlayer stop(MediaPlayer player) {
								if (player != null) {
												player.reset();
												player = null;
								}
								return player;
				}

				private void play(MediaPlayer bgPlayer) {
								if (bgPlayer != null && !bgPlayer.isPlaying()) {
												bgPlayer.start();
								}
				}


				public void playBG() {
								play(bgPlayer);
				}


				public void playSong() {
								play(songPlayer);
				}


}
