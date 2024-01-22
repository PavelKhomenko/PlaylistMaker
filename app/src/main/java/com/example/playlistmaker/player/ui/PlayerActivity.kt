package com.example.playlistmaker.player.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.library.playlists.presentation.PlaylistState
import com.example.playlistmaker.library.playlists.ui.CreatePlaylistFragment
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.domain.model.Track
import com.example.playlistmaker.player.presentation.PlayerStatus
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.search.ui.SearchFragment.Companion.SEARCH_INPUT_KEY
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {


    private val ivBackButton: ImageView by lazy { findViewById(R.id.back_arrow) }
    private val ivSongCover: ImageView by lazy { findViewById(R.id.cover) }
    private val tvSongName: TextView by lazy { findViewById(R.id.song_name) }
    private val tvSongArtist: TextView by lazy { findViewById(R.id.song_artist) }
    private val tvSongDuration: TextView by lazy { findViewById(R.id.duration) }
    private val tvSongAlbum: TextView by lazy { findViewById(R.id.album_name) }
    private val tvAlbumTitle: TextView by lazy { findViewById(R.id.album_title) }
    private val tvSongYear: TextView by lazy { findViewById(R.id.year) }
    private val tvSongGenre: TextView by lazy { findViewById(R.id.genre) }
    private val tvSongCountry: TextView by lazy { findViewById(R.id.country) }
    private val btPlay: ImageView by lazy { findViewById(R.id.play_button) }
    private val btLike: ImageView by lazy { findViewById(R.id.like_button) }
    private val btPlaylist: ImageView by lazy { findViewById(R.id.save_button) }
    private val tvSecondsPassed: TextView by lazy { findViewById(R.id.time_played) }

    private val overlay: View by lazy { findViewById(R.id.overlay) }
    private val bottomSheet: LinearLayout by lazy { findViewById(R.id.design_bottom_sheet) }
    private val btAddNewPlaylist: Button by lazy { findViewById(R.id.btAddNewPlaylist) }
    private val rvBottomSheet: RecyclerView by lazy { findViewById(R.id.rvBottomSheet) }
    private lateinit var bottomSheetAdapter: BottomSheetAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var isInsidePlaylist: Boolean = false

    private val viewModel by viewModel<PlayerViewModel>()
    private lateinit var track: Track
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        tvSongAlbum.visibility = View.VISIBLE
        tvAlbumTitle.visibility = View.VISIBLE

        track = intent.getSerializableExtra(SEARCH_INPUT_KEY) as Track
        getData(track)
        viewModel.isLiked(track)
        viewModel.preparePlayer(track.previewUrl)
        viewModel.playerStatusLiveData().observe(this) {
            when (it) {
                PlayerStatus.OnPause -> setPlayImage()
                PlayerStatus.OnStart -> setPauseImage()
            }
        }
        viewModel.getDurationLiveData().observe(this) {
            updateTimePlayed(it)
        }
        viewModel.getFavoritesLiveData().observe(this) {
            if (it) setLikeImage() else setDislikeImage()
        }
        viewModel.getPlaylistStateLiveData().observe(this) { state ->
            when (state) {
                PlaylistState.Empty -> {}
                is PlaylistState.Content -> {
                    bottomSheetAdapter.playlists.clear()
                    bottomSheetAdapter.playlists.addAll(state.playlistList)
                    bottomSheetAdapter.notifyDataSetChanged()
                }
            }
        }
        viewModel.observeInsidePlaylist().observe(this) {
            isInsidePlaylist = it
        }
        setupListeners(track)
        setupAdapters()
        setupBottomSheet()
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }

                    else -> overlay.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                overlay.alpha = slideOffset
            }

        })
    }

    private fun setupAdapters() {
        bottomSheetAdapter = BottomSheetAdapter { playlist ->
            viewModel.isInsidePlaylist(track, playlist)
            if (isInsidePlaylist) {
                Toast.makeText(
                    this, "Трек уже добавлен в плейлист ${playlist.playlistName}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                Toast.makeText(
                    this, "Добавлено в плейлист ${playlist.playlistName}",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.isInsidePlaylist(track, playlist)
                viewModel.addTrackInPlaylist(track)
            }
        }
        rvBottomSheet.adapter = bottomSheetAdapter
        rvBottomSheet.layoutManager = LinearLayoutManager(this)
        rvBottomSheet.adapter?.notifyDataSetChanged()
    }

    private fun setupListeners(track: Track) {
        ivBackButton.setOnClickListener { finish() }
        btPlay.setOnClickListener {
            viewModel.onBtnPlayClicked()
        }
        btLike.setOnClickListener {
            viewModel.onBtnFavoritesClicked(track)
        }
        btPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
        btAddNewPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val intent = Intent(this, CreatePlaylistFragment::class.java)
            startActivity(intent)
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
            .into(ivSongCover)

        tvSongDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)
        tvSongAlbum.text = track.collectionName.ifEmpty {
            ({
                tvSongAlbum.visibility = View.GONE
                tvAlbumTitle.visibility = View.GONE
            }).toString()
        }

        tvSongName.text = track.trackName
        tvSongArtist.text = track.artistName
        tvSongYear.text = track.releaseDate.subSequence(ZERO, FOUR)
        tvSongGenre.text = track.primaryGenreName
        tvSongCountry.text = track.country
        tvSecondsPassed.text = TIMER_BEGIN_TIME
    }

    private fun setPauseImage() {
        btPlay.setImageResource(R.drawable.pause)
    }

    private fun setPlayImage() {
        btPlay.setImageResource(R.drawable.play)
    }

    private fun setLikeImage() {
        btLike.setImageResource(R.drawable.like)
    }

    private fun setDislikeImage() {
        btLike.setImageResource(R.drawable.dislike)
    }

    private fun updateTimePlayed(timePlayed: String) {
        tvSecondsPassed.text = timePlayed
    }

    companion object {
        private const val TIMER_BEGIN_TIME = "00:00"
        private const val FOUR = 4
        private const val ZERO = 0
        private const val DELIMITER = '/'
        private const val REPLACEMENTS = "512x512bb.jpg"
    }
}