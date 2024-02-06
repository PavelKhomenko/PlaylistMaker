package com.example.playlistmaker.search.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.player.domain.model.Track

class TrackAdapter(
    var onItemClick: ((Track) -> Unit)? = null,
    var onItemLongClick: ((Track) -> Unit)? = null
) :
    RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder(parent)

    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(tracks[position])
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClick?.invoke(tracks[position])
            return@setOnLongClickListener true
        }
    }

}

/*
fun interface TrackClickListener {
    fun onTrackClick(track: Track)
}*/
