package com.example.mediaplayerdemo;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private List<Song> songList;
    private Context context;
    MutableLiveData<Song> songLiveData = new MutableLiveData<>();
    private int currentSong = 0;


    public SongAdapter(List<Song> songList, Context context) {
        this.songList = songList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Song song = songList.get(position);
        holder.tvSongName.setText(song.title);
        if (position == currentSong) {
            holder.trSong.setBackgroundColor(context.getColor(R.color.orange));
            holder.tvSongName.setSelected(true);
        } else {
            holder.trSong.setBackgroundColor(context.getColor(R.color.white));
            holder.tvSongName.setSelected(false);
        }
        if (song.cover != Uri.EMPTY) {

            holder.ivSongimage.setImageURI(song.cover);
        } else {
            holder.ivSongimage.setImageResource(R.drawable.ic_music_note);
        }

        holder.trSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                songLiveData.postValue(song);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (songList != null) {
            return songList.size();
        }
        return 0;
    }

    public MutableLiveData<Song> getSongLiveData() {
        return songLiveData;
    }

    public void updateUI(int currentSong) {
        this.currentSong = currentSong;
        notifyItemRangeChanged(0, songList.size());

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TableRow trSong;
        TextView tvSongName;
        ImageView ivSongimage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSongName = itemView.findViewById(R.id.tv_song_name);
            ivSongimage = itemView.findViewById(R.id.iv_song_image);
            trSong = itemView.findViewById(R.id.tr_song);
        }
    }
}