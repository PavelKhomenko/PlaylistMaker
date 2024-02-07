package com.example.playlistmaker.library.playlists.ui

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistEditBinding
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import com.example.playlistmaker.library.playlists.presentation.EditPlaylistViewModel
import com.example.playlistmaker.utils.Const.PLAYLIST_KEY
import com.example.playlistmaker.utils.MediaNameGenerator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class EditPlaylistFragment : Fragment() {
    private lateinit var playlist: Playlist
    private var addUri: Uri? = null
    private val playlistList: List<String> = mutableListOf()
    private lateinit var dialog: AlertDialog
    private val viewModel by viewModel<EditPlaylistViewModel>()
    private var _binding: FragmentPlaylistEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDialog()
        setupTextWatcher()
        photoPicker()
        setupListeners()
        playlist = requireArguments().getSerializable(PLAYLIST_KEY)!!as Playlist
        viewModel.getData(playlist)
        viewModel.getplaylistLiveData().observe(viewLifecycleOwner) {
            binding.playlistNameEditText.setText(playlist.playlistName)
            binding.playlistDescriptionEditText.setText(playlist.playlistDescription)
            Glide.with(requireActivity())
                .load(playlist.playlistUri)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(binding.ivPlaylistCoverEdit)
        }
        viewModel.getButtonLiveData().observe(viewLifecycleOwner) {
            binding.btEditPlaylist.isEnabled = it
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isNoData()) {
                    dialog.show()
                } else {
                   findNavController().navigate(R.id.action_editPlaylistFragment_to_playlistDetailsFragment,
                    createArg(playlist))
                }
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
    }

    private fun setupListeners() {
        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_editPlaylistFragment_to_playlistDetailsFragment,
                createArg(playlist))
        }
        binding.btEditPlaylist.setOnClickListener { button ->
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
                    .setPopUpTo(R.id.editPlaylistFragment, true) // Замените R.id.editPlaylistFragment на идентификатор вашего фрагмента
                    .build()
                findNavController().navigate(
                    R.id.action_editPlaylistFragment_to_playlistDetailsFragment,
                    createArg(tempPlayist), navOptions
                )
                val message = "Плейлист ${binding.playlistNameEditText.text} сохранён"
                Snackbar.make(
                    requireContext(),
                    binding.btEditPlaylist,
                    message,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupTextWatcher() {

        val textWatcherName = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    binding.playlistNameTextInputLayout.setInputBoxColor(R.drawable.box_layout_selected)
                    viewModel.hasPlaylistName(true)
                } else {
                    binding.playlistNameTextInputLayout.setInputBoxColor(R.drawable.box_layout_unselected)
                    viewModel.hasPlaylistName(true)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        }
        binding.playlistNameEditText.addTextChangedListener(textWatcherName)

        val textWatcherDescription = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    binding.playlistDescriptionTextInputLayout.setInputBoxColor(R.drawable.box_layout_selected)
                } else {
                    binding.playlistDescriptionTextInputLayout.setInputBoxColor(R.drawable.box_layout_unselected)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        binding.playlistDescriptionEditText.addTextChangedListener(textWatcherDescription)
    }

    private fun TextInputLayout.setInputBoxColor(colorId: Int) {
        this.defaultHintTextColor =
            resources.getColorStateList(colorId, null)
        this.setBoxStrokeColorStateList(resources.getColorStateList(colorId, null))
    }

    private fun photoPicker() {
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    val mediaName = MediaNameGenerator.generateName()
                    binding.ivPlaylistCoverEdit.setImageURI(uri)
                    addUri = saveImageToPrivateStorage(uri, mediaName)
                }
            }
        binding.ivPlaylistCoverEdit.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri, mediaName: String): Uri {
        val filePath =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")

        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, mediaName)
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        return file.toUri()
    }

    private fun setupDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setTitle("Завершить изменение плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNeutralButton("Отмена") { _, _ ->
            }
            .setPositiveButton("Завершить") { _, _ ->
                findNavController().popBackStack()
            }
        dialog = builder.create()
    }

    private fun isNoData(): Boolean {
        return (addUri != null
                || binding.playlistNameEditText.text.isNotEmpty()
                || binding.playlistDescriptionEditText.text.isNotEmpty())
    }

    fun createArg(playlist: Playlist) : Bundle {
        return bundleOf(PLAYLIST_KEY to playlist)
    }
}