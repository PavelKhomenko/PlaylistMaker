package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val ivSongCover: ImageView = itemView.findViewById(R.id.songCover)
    private val tvSongName: TextView = itemView.findViewById(R.id.songName)
    private val tvSongArtist: TextView = itemView.findViewById(R.id.songArtist)
    private val tvSongDuration: TextView = itemView.findViewById(R.id.songDuration)

    fun bind(model: Track) {
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(25))
            .centerInside()
            .into(ivSongCover)

        tvSongName.text = model.trackName
        tvSongArtist.text = model.artistName
        tvSongDuration.text = model.trackTime
    }

}