package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentLibraryBinding
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.library.playlists.presentation.PlaylistState
import com.example.playlistmaker.player.domain.model.Track
import com.example.playlistmaker.player.presentation.PlayerStatus
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.search.ui.SearchFragment.Companion.SEARCH_INPUT_KEY
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {

    private lateinit var track: Track
    private lateinit var bottomSheetAdapter: BottomSheetAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private var isInsidePlaylist: Boolean = true
    private val viewModel by viewModel<PlayerViewModel>()
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.albumName.visibility = View.VISIBLE
        binding.albumTitle.visibility = View.VISIBLE
        track = requireArguments().getSerializable(SEARCH_INPUT_KEY) as Track
        getData(track)
        viewModel.isLiked(track)
        viewModel.preparePlayer(track.previewUrl)
        viewModel.playerStatusLiveData().observe(viewLifecycleOwner) {
            when (it) {
                PlayerStatus.OnPause -> setPlayImage()
                PlayerStatus.OnStart -> setPauseImage()
            }
        }
        viewModel.getDurationLiveData().observe(viewLifecycleOwner) {
            updateTimePlayed(it)
        }
        viewModel.getFavoritesLiveData().observe(viewLifecycleOwner) {
            if (it) setLikeImage() else setDislikeImage()
        }
        viewModel.getPlaylistStateLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                PlaylistState.Empty -> {}
                is PlaylistState.Content -> {
                    bottomSheetAdapter.playlists.clear()
                    bottomSheetAdapter.playlists.addAll(state.playlistList)
                    bottomSheetAdapter.notifyDataSetChanged()
                }
            }
        }
        viewModel.observeInsidePlaylist().observe(viewLifecycleOwner) {
            isInsidePlaylist = it
        }
        setupListeners(track)
        setupAdapters()
        setupBottomSheet()
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.designBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> binding.overlay.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset
            }
        })
    }

    private fun setupAdapters() {
        bottomSheetAdapter = BottomSheetAdapter { playlist ->
            viewModel.isInsidePlaylist(track, playlist)
            if (isInsidePlaylist) {
                Toast.makeText(
                    context, "Трек уже добавлен в плейлист ${playlist.playlistName}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                Toast.makeText(
                    context, "Добавлено в плейлист ${playlist.playlistName}",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.updatePlaylist(track, playlist)
                viewModel.addTrackInPlaylist(track)
            }
        }
        binding.rvBottomSheet.adapter = bottomSheetAdapter
        binding.rvBottomSheet.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBottomSheet.adapter?.notifyDataSetChanged()
    }

    private fun setupListeners(track: Track) {
        binding.backArrow.setOnClickListener { findNavController().popBackStack() }
        binding.playButton.setOnClickListener {
            viewModel.onBtnPlayClicked()
        }
        binding.likeButton.setOnClickListener {
            viewModel.onBtnFavoritesClicked(track)
        }
        binding.saveButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
        binding.btAddNewPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(R.id.action_playerFragment_to_createPlaylistFragment)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onViewPaused()
    }

    private fun getData(track: Track) {

        Glide.with(this).load(track.artworkUrl100.replaceAfterLast(DELIMITER, REPLACEMENTS))
            .placeholder(R.drawable.placeholder).centerInside()
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.margin_8dp)))
            .into(binding.cover)

        binding.duration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)
        binding.albumName.text = track.collectionName.ifEmpty {
            ({
                binding.albumName.visibility = View.GONE
                binding.albumTitle.visibility = View.GONE
            }).toString()
        }

        binding.songName.text = track.trackName
        binding.songArtist.text = track.artistName
        binding.year.text = track.releaseDate.subSequence(ZERO, FOUR)
        binding.genre.text = track.primaryGenreName
        binding.country.text = track.country
        binding.timePlayed.text = TIMER_BEGIN_TIME
    }

    private fun setPauseImage() {
        binding.playButton.setImageResource(R.drawable.pause)
    }

    private fun setPlayImage() {
        binding.playButton.setImageResource(R.drawable.play)
    }

    private fun setLikeImage() {
        binding.likeButton.setImageResource(R.drawable.like)
    }

    private fun setDislikeImage() {
        binding.likeButton.setImageResource(R.drawable.dislike)
    }

    private fun updateTimePlayed(timePlayed: String) {
        binding.timePlayed.text = timePlayed
    }

    companion object {
        private const val TIMER_BEGIN_TIME = "00:00"
        private const val FOUR = 4
        private const val ZERO = 0
        private const val DELIMITER = '/'
        private const val REPLACEMENTS = "512x512bb.jpg"
    }
}