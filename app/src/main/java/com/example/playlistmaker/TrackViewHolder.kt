package com.example.playlistmaker

import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale


class TrackViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.track_view, parentView, false)
) {

    private val ivSongCover: ImageView = itemView.findViewById(R.id.songCover)
    private val tvSongName: TextView = itemView.findViewById(R.id.songName)
    private val tvSongArtist: TextView = itemView.findViewById(R.id.songArtist)
    private val tvSongDuration: TextView = itemView.findViewById(R.id.songDuration)


    fun bind(track: Track) {
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(2))
            .centerInside()
            .into(ivSongCover)
        tvSongName.text = track.trackName
        tvSongArtist.text = track.artistName
        tvSongDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)
    }
}