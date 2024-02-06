package com.example.playlistmaker.library.playlistDetails.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.library.playlistDetails.presentation.PlaylistDetailsViewModel
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import com.example.playlistmaker.player.domain.model.Track
import com.example.playlistmaker.player.ui.BottomSheetAdapter
import com.example.playlistmaker.search.ui.SearchFragment.Companion.SEARCH_INPUT_KEY
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.utils.Const.PLAYLIST_KEY
import com.example.playlistmaker.utils.ConvertUlits
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistDetailsFragment : Fragment() {
    val viewModel by viewModel<PlaylistDetailsViewModel>()
    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var playlist: Playlist
    private lateinit var trackInPlaylist: List<Track>
    private lateinit var trackAdapter: TrackAdapter

    //private lateinit var bottomSheetAdapter: BottomSheetAdapter
    //private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var dialog: MaterialAlertDialogBuilder
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
            trackAdapter = TrackAdapter()
            trackAdapter.onItemClick = {
                findNavController().navigate(
                    R.id.action_playlistDetailsFragment_to_playerFragment,
                    bundleOf(SEARCH_INPUT_KEY to it)
                )
            }
            trackAdapter.onItemLongClick = {
                dialog = MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Хотите удалить трек?")
                    .setNegativeButton("Нет") { _, _ ->
                    }
                    .setPositiveButton("Да") { _, _ ->
                        viewModel.deleteTrack(it.trackId, playlist.id)
                    }
                dialog.show()
            }
            trackAdapter.tracks.clear()
            trackAdapter.tracks.addAll(tracks)
            binding.rvTracksFromPlaylist.adapter = trackAdapter
            binding.rvTracksFromPlaylist.layoutManager = LinearLayoutManager(requireContext())
            trackAdapter.notifyDataSetChanged()
        }
        initView(view)
        setupAdapters()
        initListeners(playlist)
        setupBottomSheet()
    }

    private fun setupBottomSheet() {

    }

    private fun setupAdapters() {

    }

    private fun initListeners(playlist: Playlist) {
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

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