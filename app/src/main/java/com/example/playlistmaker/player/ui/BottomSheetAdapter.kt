package com.example.playlistmaker.player.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import com.example.playlistmaker.library.playlists.ui.PlaylistViewHolder

class BottomSheetAdapter(private val itemClickListener: PlaylistClickListener):
    RecyclerView.Adapter<BottomSheetViewHolder>() {

    var playlists = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : BottomSheetViewHolder =
        BottomSheetViewHolder(parent)

    override fun getItemCount() = playlists.size

    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(playlists[position])
        }
    }

    fun interface PlaylistClickListener {
        fun onClick(playlist: Playlist)
    }
}