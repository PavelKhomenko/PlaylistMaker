package com.example.playlistmaker.library.playlists.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import com.example.playlistmaker.library.playlists.presentation.PlaylistState
import com.example.playlistmaker.library.playlists.presentation.PlaylistViewModel
import com.example.playlistmaker.utils.Const.PLAYLIST_KEY
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private lateinit var rvPlaylist: RecyclerView
    private lateinit var playlistAdapter : PlaylistAdapter

    private val viewModel by viewModel<PlaylistViewModel>()
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        binding.btAddPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistFragment_to_createPlaylistFragment
            )
        }
        viewModel.state().observe(viewLifecycleOwner) {state ->
            when(state) {
                PlaylistState.Empty -> {
                    binding.placeholderEmptyLibrary.visibility = View.VISIBLE
                    binding.rvPlaylist.visibility = View.GONE
                }
                is PlaylistState.Content -> {
                    binding.placeholderEmptyLibrary.visibility = View.GONE
                    binding.rvPlaylist.visibility = View.VISIBLE
                    content(state.playlistList)
                }
            }
        }
    }

    private fun setupAdapters() {
        val onClickListener = PlaylistAdapter.PlaylistClickListener {
            findNavController().navigate(
                R.id.action_libraryFragment_to_playlistDetailsFragment,
                bundleOf(PLAYLIST_KEY to it)
            )
        }
        playlistAdapter = PlaylistAdapter(onClickListener)
        binding.rvPlaylist.adapter = playlistAdapter
        binding.rvPlaylist.layoutManager = GridLayoutManager(requireContext(),2)
    }

    private fun content(playlistList: List<Playlist>) {
        playlistAdapter.playlists.clear()
        playlistAdapter.playlists.addAll(playlistList)
        playlistAdapter.notifyDataSetChanged()
    }

    companion object {
        fun newInstance() = PlaylistFragment()
    }
}