package com.example.playlistmaker.library.favorites.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.library.favorites.presentation.FavoritesState
import com.example.playlistmaker.library.favorites.presentation.FavoritesViewModel
import com.example.playlistmaker.player.domain.model.Track
import com.example.playlistmaker.search.ui.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.search.ui.SearchFragment.Companion.SEARCH_INPUT_KEY
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var onClickDebounce: (Track) -> Unit

    private var isClickAllowed = true

    private val viewModel by viewModel<FavoritesViewModel>()
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                FavoritesState.Empty -> {
                    binding.FavoritesEmpty.visibility = View.VISIBLE
                    binding.recyclerLiked.visibility = View.GONE
                }

                is FavoritesState.Content -> {
                    binding.FavoritesEmpty.visibility = View.GONE
                    binding.recyclerLiked.visibility = View.VISIBLE
                    content(state.favoriteTracks)
                }
            }
        }
        onClickDebounce =
            debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false, action = {
                findNavController().navigate(
                    R.id.action_libraryFragment_to_playerFragment, bundleOf(
                        SEARCH_INPUT_KEY to it
                    )
                )
            })
    }

    private fun setupAdapters() {
        trackAdapter = TrackAdapter()
        trackAdapter.onItemClick = {
            onClickDebounce(it)
        }
        binding.recyclerLiked.adapter = trackAdapter
    }

    private fun content(liked: List<Track>) {
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(liked)
        trackAdapter.notifyDataSetChanged()

    }

    companion object {
        const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
        fun newInstance() = FavoritesFragment()
    }
}