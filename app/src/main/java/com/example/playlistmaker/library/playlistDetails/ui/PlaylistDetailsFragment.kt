package com.example.playlistmaker.library.playlistDetails.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.library.playlistDetails.presentation.PlaylistDetailsViewModel
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import com.example.playlistmaker.player.domain.model.Track
import com.example.playlistmaker.search.ui.SearchFragment.Companion.SEARCH_INPUT_KEY
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.utils.Const.PLAYLIST_KEY
import com.example.playlistmaker.utils.ConvertUlits
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistDetailsFragment : Fragment() {
    val viewModel by viewModel<PlaylistDetailsViewModel>()
    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var playlist: Playlist
    private lateinit var trackInPlaylist: List<Track>
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                createConfirmationDialog(
                    requireContext(),
                    "Хотите удалить трек?",
                    "Да",
                    "Нет",
                    {
                        viewModel.deleteTrack(it.trackId, playlist.id)
                    },
                    {
                        //no action
                    })
            }
            trackAdapter.tracks.clear()
            trackAdapter.tracks.addAll(tracks)
            binding.rvTracksFromPlaylist.adapter = trackAdapter
            binding.rvTracksFromPlaylist.layoutManager = LinearLayoutManager(requireContext())
            trackAdapter.notifyDataSetChanged()
        }
        initView(view)
        initListeners(playlist)
        setupBottomSheet()
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
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
                binding.overlay.alpha = slideOffset + 1
            }
        })
    }

    private fun initListeners(playlist: Playlist) {
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btSharePlaylist.setOnClickListener { sharePlaylist() }
        binding.btOptionPlaylist.setOnClickListener {
            Glide.with(requireActivity())
                .load(playlist.playlistUri)
                .placeholder(R.drawable.placeholder).centerInside()
                .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.margin_8dp)))
                .into(binding.menuPlaylistCover)
            binding.menuPlaylistName.text = playlist.playlistName
            binding.menuPlaylistSize.text =
                ConvertUlits().convertSizeToString(playlist.playlistSize)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
        binding.tvMenuSharePlaylist.setOnClickListener { sharePlaylist() }
        binding.tvDeletePlaylist.setOnClickListener {
            createConfirmationDialog(
                requireContext(),
                "Хотите удалить плейлист ${playlist.playlistName}?",
                "Да",
                "Нет",
                {
                    viewModel.deletePlaylist(playlist)
                    findNavController().popBackStack()
                },
                {
                    //no action
                })
        }
        binding.tvMenuChangeInfo.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.playlistDetailsFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_playlistDetailsFragment_to_editPlaylistFragment,
                createArg(playlist), navOptions
            )

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
        if (playlist.playlistTracks.isEmpty()) {
            binding.emptyPlaylistPlaceholder.visibility = View.VISIBLE
        } else binding.emptyPlaylistPlaceholder.visibility = View.GONE
    }

    private fun sharePlaylist() {
        if (playlist.playlistTracks.isEmpty()) {
            val message = "В этом плейлисте нет списка треков, которым можно поделиться"
            Snackbar.make(
                requireContext(),
                binding.bottomSheetWithTracks,
                message,
                Snackbar.LENGTH_SHORT
            ).show()
        } else {
            val shareIntentLink = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, getPlaylistDataToShare(playlist, trackInPlaylist))
                type = "text/plain"
            }
            requireActivity().startActivity(shareIntentLink.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    private fun getPlaylistDataToShare(playlist: Playlist, trackList: List<Track>): String {
        val dataToShare = mutableListOf<String>()
        dataToShare.add(playlist.playlistName)
        if (!playlist.playlistDescription.isNullOrEmpty()) dataToShare.add(playlist.playlistDescription)
        dataToShare.add(ConvertUlits().convertSizeToString(playlist.playlistSize))
        trackList.forEachIndexed { index, track ->
            dataToShare.add(
                "${index + 1} ${track.artistName} - ${track.trackName}"
            )
        }
        return dataToShare.joinToString("\n")
    }

    private fun createConfirmationDialog(
        context: Context,
        title: String,
        positiveButtonText: String,
        negativeButtonText: String,
        positiveAction: () -> Unit,
        negativeAction: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context, R.style.MyDialogTheme)
            .setTitle(title)
            .setNegativeButton(negativeButtonText) { _, _ ->
                negativeAction.invoke()
            }
            .setPositiveButton(positiveButtonText) { _, _ ->
                positiveAction.invoke()
            }
            .show()
    }

    companion object {
        fun createArg(playlist: Playlist): Bundle {
            return bundleOf(PLAYLIST_KEY to playlist)
        }
    }
}