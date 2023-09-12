package com.example.playlistmaker

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.SearchActivity.Companion.SEARCH_INPUT_KEY
import java.text.SimpleDateFormat
import java.util.Locale

private const val FOUR = 4
private const val ZERO = 0
private const val DELIMITER = '/'
private const val PROPER_DIMENSIONS = "512x512bb.jpg"

class PlayerActivity : AppCompatActivity() {

    private var mainThreadHandler: Handler? = null
    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()

    private lateinit var url: String
    private lateinit var track: Track

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        tvSongAlbum.visibility = View.VISIBLE
        tvAlbumTitle.visibility = View.VISIBLE
        ivBackButton.setOnClickListener { finish() }

        tvSecondsPassed.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)

        mainThreadHandler = Handler(Looper.getMainLooper())

        getData()
        preparePlayer()

        btPlay.setOnClickListener {
            playbackControl()
        }
    }

    private fun getData() {
        track = intent.getParcelableExtra<Track>(SEARCH_INPUT_KEY) ?: return

        Glide.with(this).load(track.artworkUrl100?.replaceAfterLast(DELIMITER, PROPER_DIMENSIONS))
            .placeholder(R.drawable.placeholder).centerInside()
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.margin_8dp)))
            .into(ivSongCover)

        tvSongDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)
        tvSongAlbum.text = if (track.collectionName.isNullOrEmpty()) ({
            tvSongAlbum.visibility = View.GONE
            tvAlbumTitle.visibility = View.GONE
        }).toString() else track.collectionName

        tvSongName.text = track.trackName
        tvSongArtist.text = track.artistName
        tvSongYear.text = track.releaseDate.subSequence(ZERO, FOUR)
        tvSongGenre.text = track.primaryGenreName
        tvSongCountry.text = track.country
        url = track.previewUrl
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler?.removeCallbacksAndMessages(null)
        mediaPlayer.release()
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            btPlay.isEnabled = true
            btPlay.setImageResource(R.drawable.play)
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            btPlay.setImageResource(R.drawable.play)
            setDuration(0)
            playerState = STATE_PREPARED
            mainThreadHandler?.removeCallbacksAndMessages(null)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        btPlay.setImageResource(R.drawable.pause)
        playerState = STATE_PLAYING
        mainThreadHandler?.postDelayed(object : Runnable {
            override fun run() {
                setDuration(mediaPlayer.currentPosition)
                mainThreadHandler?.postDelayed(this, DELAY)
            }
        }, DELAY)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        btPlay.setImageResource(R.drawable.play)
        playerState = STATE_PAUSED
    }

    private fun setDuration(milliseconds: Int) {
        tvSecondsPassed.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(milliseconds)
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 1000L
    }
}