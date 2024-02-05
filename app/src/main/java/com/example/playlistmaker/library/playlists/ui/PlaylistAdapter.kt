package com.example.playlistmaker.library.playlists.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import com.example.playlistmaker.search.ui.TrackViewHolder

class PlaylistAdapter(private val itemClickListener: PlaylistClickListener):
    RecyclerView.Adapter<PlaylistViewHolder>() {

        var playlists = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : PlaylistViewHolder =
        PlaylistViewHolder(parent)

    override fun getItemCount() = playlists.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(playlists[position])
        }
    }

    fun interface PlaylistClickListener {
        fun onClick(playlist: Playlist)
    }
}