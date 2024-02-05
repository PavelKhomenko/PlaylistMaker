package com.example.playlistmaker.library.playlistDetails.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.library.playlistDetails.presentation.PlaylistDetailsViewModel
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import com.example.playlistmaker.player.domain.model.Track
import com.example.playlistmaker.utils.Const.PLAYLIST_KEY
import com.example.playlistmaker.utils.ConvertUlits
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistDetailsFragment : Fragment() {
    val viewModel by viewModel<PlaylistDetailsViewModel>()
    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var playlist: Playlist
    private lateinit var trackInPlaylist: List<Track>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlist = requireArguments().getSerializable(PLAYLIST_KEY)!! as Playlist
        viewModel.getData(playlist)
        viewModel.getTracks().observe(viewLifecycleOwner) { tracks ->
            trackInPlaylist = tracks
            binding.playlistDuration.text = ConvertUlits().conventDurationToMinutesString(
                ConvertUlits().formatTimeMillisToMinutesString(tracks.sumOf { it.trackTime })
            )
        }
        initView(view)
    }

    private fun initView(view: View) {
        Glide.with(requireActivity())
            .load(playlist.playlistUri)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(binding.playlistCover)

        binding.playlistName.text = playlist.playlistName
        binding.playlistDescription.text = playlist.playlistDescription
        binding.playlistSize.text = ConvertUlits().convertSizeToString(playlist.playlistSize)

    }

}