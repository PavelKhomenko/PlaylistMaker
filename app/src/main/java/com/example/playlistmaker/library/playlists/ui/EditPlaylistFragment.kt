package com.example.playlistmaker.library.playlists.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistCreateBinding
import com.example.playlistmaker.library.playlistDetails.ui.PlaylistDetailsFragment.Companion.createArg
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import com.example.playlistmaker.library.playlists.presentation.EditPlaylistViewModel
import com.example.playlistmaker.utils.Const.PLAYLIST_KEY
import com.example.playlistmaker.utils.MediaNameGenerator
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel


class EditPlaylistFragment : CreatePlaylistFragment() {
    private lateinit var playlist: Playlist
    override val viewModel by viewModel<EditPlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupTextWatcher()
        photoPicker()
        setupListeners()
        playlist = requireArguments().getSerializable(PLAYLIST_KEY)!! as Playlist
        viewModel.getData(playlist)
        viewModel.getButtonLiveData().observe(viewLifecycleOwner) {
            binding.btCreateNew.isEnabled = it
        }
    }

    override fun setupListeners() {
        super.setupListeners()
        binding.btCreateNew.setOnClickListener { button ->
            var tempPlayist: Playlist? = null
            if (button.isEnabled) {
                if (addUri == null)
                    addUri = Uri.parse(playlist.playlistUri)
                tempPlayist = playlist.copy(
                    id = playlist.id,
                    playlistName = binding.playlistNameEditText.text.toString(),
                    playlistDescription = binding.playlistDescriptionEditText.text.toString(),
                    playlistUri = addUri.toString(),
                    playlistTracks = playlist.playlistTracks,
                    playlistSize = playlist.playlistSize
                )
                viewModel.editPlaylist(tempPlayist)
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(
                        R.id.editPlaylistFragment,
                        true
                    )
                    .build()
                findNavController().navigate(
                    R.id.action_editPlaylistFragment_to_playlistDetailsFragment,
                    createArg(tempPlayist), navOptions
                )
                val message = "Плейлист ${binding.playlistNameEditText.text} сохранён"
                Snackbar.make(
                    requireContext(),
                    binding.btCreateNew,
                    message,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        binding.back.setOnClickListener {
            back()
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                back()
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
    }

    private fun setupUI() {
        viewModel.getplaylistLiveData().observe(viewLifecycleOwner) {
            binding.playlistNameEditText.setText(playlist.playlistName)
            binding.playlistDescriptionEditText.setText(playlist.playlistDescription)
            Glide.with(requireActivity())
                .load(playlist.playlistUri)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(binding.playlistCover)
        }
        binding.back.title = "Редактировать плейлист"
        binding.btCreateNew.text = "Сохранить"
    }

    private fun back() {
        viewModel.editPlaylist(playlist)
        val navOptions = NavOptions.Builder()
            .setPopUpTo(
                R.id.editPlaylistFragment,
                true
            )
            .build()
        findNavController().navigate(
            R.id.action_editPlaylistFragment_to_playlistDetailsFragment,
            createArg(playlist), navOptions
        )
    }
}