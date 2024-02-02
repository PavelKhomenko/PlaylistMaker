package com.example.playlistmaker.library.playlists.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import com.example.playlistmaker.search.ui.TrackClickListener

class PlaylistViewHolder(parentView: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.playlist_view, parentView, false)
    ) {

    private val ivPlaylistCover: ImageView = itemView.findViewById(R.id.playlist_cover)
    private val tvPlaylistName: TextView = itemView.findViewById(R.id.playlist_name)
    private val tvPlaylistSize: TextView = itemView.findViewById(R.id.playlist_size)

    fun bind(playlist: Playlist) {
        val uri = Uri.parse(playlist.playlistUri)
        Glide.with(itemView)
            .load(uri)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.margin_8dp)))
            .into(ivPlaylistCover)
        tvPlaylistName.text = playlist.playlistName
        tvPlaylistSize.text = convertSizeToString(playlist.playlistSize)
    }

    private fun convertSizeToString(playlistSize: Int): String {
        val preLastDigit = playlistSize % 100
        if (preLastDigit in 5..20) return "$playlistSize треков"
        return when (preLastDigit % 10) {
            1 -> "$playlistSize трек"
            in(2..4) -> "$playlistSize трека"
            else -> "$playlistSize треков"
        }
    }
}