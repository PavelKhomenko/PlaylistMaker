package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.model.Track
import com.example.playlistmaker.player.presentation.PlayerStatus
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.search.ui.SearchFragment.Companion.SEARCH_INPUT_KEY
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
    private val tvSecondsPassed: TextView by lazy { findViewById(R.id.time_played) }

    private val viewModel by viewModel<PlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        tvSongAlbum.visibility = View.VISIBLE
        tvAlbumTitle.visibility = View.VISIBLE

        val track = intent.getSerializableExtra(SEARCH_INPUT_KEY) as Track
        getData(track)

        viewModel.preparePlayer(track.previewUrl)
        viewModel.getPlayerStatusLiveData().observe(this) {
            when (it) {
                PlayerStatus.OnPause -> setPlayImage()
                PlayerStatus.OnStart -> setPauseImage()
            }
        }
        viewModel.getDurationLiveData().observe(this) {
            updateTimePlayed(it)
        }

        setupListeners()
    }

    private fun setupListeners() {
        ivBackButton.setOnClickListener { finish() }
        btPlay.setOnClickListener {
            viewModel.onBtnPlayClicked()
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