package com.example.playlistmaker.library.favorites.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.library.favorites.presentation.FavoritesState
import com.example.playlistmaker.library.favorites.presentation.FavoritesViewModel
import com.example.playlistmaker.player.domain.model.Track
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.ui.SearchFragment
import com.example.playlistmaker.search.ui.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.search.ui.SearchFragment.Companion.SEARCH_INPUT_KEY
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.search.ui.TrackClickListener
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private lateinit var placeholder: LinearLayout
    private lateinit var rvLikedTrack: RecyclerView
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
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupAdapters()
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                FavoritesState.Empty -> {
                    placeholder.visibility = View.GONE
                    rvLikedTrack.visibility = View.VISIBLE
                }

                is FavoritesState.Content -> {
                    placeholder.visibility = View.GONE
                    rvLikedTrack.visibility = View.VISIBLE
                    content(state.tracks)
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

    private fun initViews() {
        placeholder = binding.FavoritesEmpty
        rvLikedTrack = binding.recyclerLiked
    }

    private fun setupAdapters() {
        trackAdapter = TrackAdapter {
            onClickDebounce(it)
        }
        rvLikedTrack.adapter = trackAdapter
    }

    private fun content(liked: List<Track>) {
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(liked)
        trackAdapter.notifyDataSetChanged()
    }

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 1000L
        fun newInstance() = FavoritesFragment()
    }
}