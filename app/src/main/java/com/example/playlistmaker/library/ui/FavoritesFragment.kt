package com.example.playlistmaker.library.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.library.presentation.FavoritesState
import com.example.playlistmaker.library.presentation.FavoritesViewModel
import com.example.playlistmaker.player.domain.model.Track
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.ui.SearchFragment
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.search.ui.TrackClickListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private lateinit var placeholder: LinearLayout
    private lateinit var rvLikedTrack: RecyclerView
    private lateinit var trackAdapter: TrackAdapter

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
        placeholder = view.findViewById(R.id.FavoritesEmpty)
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
    }

    private fun initViews() {
        placeholder = binding.FavoritesEmpty
        rvLikedTrack = binding.recyclerLiked
    }

    private fun setupAdapters() {
        val onClickListener = TrackClickListener { track ->
            if (clickDebounce()) {
                transferDataToPlayerActivity(track)
            }
        }
        trackAdapter = TrackAdapter(onClickListener)
        rvLikedTrack.adapter = trackAdapter
    }

    private fun content(liked: List<Track>) {
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(liked)
        trackAdapter.notifyDataSetChanged()
    }

    private fun transferDataToPlayerActivity(track: Track) {
        val intent = Intent(requireContext(), PlayerActivity::class.java)
        intent.putExtra(SearchFragment.SEARCH_INPUT_KEY, track)
        startActivity(intent)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(SearchFragment.CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }
}