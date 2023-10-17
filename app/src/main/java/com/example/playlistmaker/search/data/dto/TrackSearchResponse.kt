package com.example.playlistmaker.search.data.dto

import com.example.playlistmaker.player.domain.model.Track

class TrackSearchResponse(
    val resultCount: Int,
    val results: List<Track>
) : Response()