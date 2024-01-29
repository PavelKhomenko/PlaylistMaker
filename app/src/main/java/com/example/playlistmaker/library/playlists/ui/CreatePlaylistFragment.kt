package com.example.playlistmaker.library.playlists.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.databinding.FragmentPlaylistCreateBinding
import com.example.playlistmaker.library.playlists.domain.model.Playlist
import com.example.playlistmaker.library.playlists.presentation.CreatePlaylistViewModel
import com.example.playlistmaker.search.presentation.SearchStatus
import com.example.playlistmaker.utils.MediaNameGenerator
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistFragment : Fragment() {

    private var addUri: Uri? = null
    private val playlistList: List<String> = mutableListOf()
    private lateinit var dialog: MaterialAlertDialogBuilder
    private val viewModel by viewModel<CreatePlaylistViewModel>()
    private var _binding: FragmentPlaylistCreateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDialog()
        photoPicker()
        setupTextWatcher()
        setupListeners()
        viewModel.getButtonLiveData().observe(viewLifecycleOwner) {
            binding.btCreateNew.isEnabled = it
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isNoData()) {
                    dialog.show()
                } else {
                    findNavController().popBackStack()
                }
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
    }

    private fun setupListeners() {
        binding.back.setOnClickListener {
            if (isNoData()) {
                dialog.show()
            } else {
                findNavController().popBackStack()
            }
        }
        binding.btCreateNew.setOnClickListener { button ->
            if (button.isEnabled) {
                findNavController().popBackStack()
                Toast.makeText(
                    requireContext(),
                    "Плейлист ${binding.playlistNameEditText.text} создан",
                    Toast.LENGTH_SHORT
                ).show()
            }
            viewModel.createPlaylist(
                Playlist(
                    id = 0,
                    playlistName = binding.playlistNameEditText.text.toString(),
                    playlistDescription = binding.playlistDescriptionEditText.text.toString(),
                    playlistUri = addUri.toString(),
                    playlistTracks = playlistList,
                    playlistSize = playlistList.size
                )
            )
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
        binding.playlistNameEditText.addTextChangedListener (textWatcherName)


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
                    binding.playlistCover.setImageURI(uri)
                    addUri = saveImageToPrivateStorage(uri, mediaName)
                }
            }
        binding.playlistCover.setOnClickListener {
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
        dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNeutralButton("Отмена") { _, _ ->
            }
            .setPositiveButton("Завершить") { _, _ ->
                findNavController().popBackStack()
            }
    }

    private fun isNoData(): Boolean {
        return (addUri != null
                || binding.playlistNameEditText.text.isNotEmpty()
                || binding.playlistDescriptionEditText.text.isNotEmpty())
    }
}
