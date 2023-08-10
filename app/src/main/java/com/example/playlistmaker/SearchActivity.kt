package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val SUCCESS = 200

class SearchActivity : AppCompatActivity() {

    // Views initialization and variables declarations
    private lateinit var queryInput: EditText
    private lateinit var back: View
    private lateinit var ivClearInputText: ImageView
    private lateinit var clearHistoryButton: Button
    private lateinit var refresh: Button

    private lateinit var rvTrack: RecyclerView
    private lateinit var rvHistoryTrack: RecyclerView

    private lateinit var nothingFound: LinearLayout
    private lateinit var networkIssue: LinearLayout
    private lateinit var searchHistoryFragment: LinearLayout

    private lateinit var searchHistory: SearchHistory

    private val tracks = ArrayList<Track>()
    private val historyTracks = ArrayList<Track>()

    //Retrofit set up
    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ItunesApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchHistory = SearchHistory(getSharedPreferences(HISTORY_KEY, MODE_PRIVATE))

        initViews()
        setupListeners()
        setupAdapters()
    }

    private fun initViews() {
        back = findViewById(R.id.back)
        ivClearInputText = findViewById(R.id.clear_text)
        queryInput = findViewById(R.id.search_input)
        refresh = findViewById(R.id.refreshButton)
        clearHistoryButton = findViewById(R.id.delete_history_button)
        rvTrack = findViewById(R.id.recyclerSearch)
        rvHistoryTrack = findViewById(R.id.recycler_history)
        nothingFound = findViewById(R.id.error_nothing_found)
        networkIssue = findViewById(R.id.error_no_connection)
        searchHistoryFragment = findViewById(R.id.search_history_fragment)
    }

    private fun setupAdapters() {
        historyTracks.addAll(searchHistory.loadHistory())

        if (historyTracks.isNotEmpty())
            searchHistoryFragment.visibility = View.VISIBLE

        val trackAdapter = TrackAdapter {
            addToRecentHistoryList(it)
            //переход на трек + удалить тост
            Toast.makeText(this, "clicked", Toast.LENGTH_LONG).show()
        }
        trackAdapter.recentTracks = tracks
        rvTrack.adapter = trackAdapter

        val historyTrackAdapter = TrackAdapter {
            //переход на трек
        }
        historyTrackAdapter.recentTracks = historyTracks
        rvHistoryTrack.adapter = historyTrackAdapter
    }

    private fun setupListeners() {
        back.setOnClickListener { finish() }
        refresh.setOnClickListener { search() }

        ivClearInputText.setOnClickListener {
            queryInput.text = null
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(queryInput.windowToken, 0)
            rvTrack.visibility = View.GONE
            if (historyTracks.isEmpty()) {
                setStatus(SearchStatus.ALL_GONE)
            } else setStatus(SearchStatus.HISTORY)
        }

        queryInput.apply { requestFocus() }
        queryInput.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                ivClearInputText.visibility = View.GONE
                if (historyTracks.isEmpty()) {
                    setStatus(SearchStatus.ALL_GONE)
                } else setStatus(SearchStatus.HISTORY)
            } else {
                ivClearInputText.visibility = View.VISIBLE
                searchHistoryFragment.visibility = View.GONE
            }
        }
        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (queryInput.text.isNotEmpty())
                    search()
                else {
                    if (historyTracks.isEmpty()) {
                        setStatus(SearchStatus.ALL_GONE)
                    } else setStatus(SearchStatus.HISTORY)
                }
                true
            }
            false
        }
        queryInput.setOnFocusChangeListener { view, hasFocus ->
            searchHistoryFragment.visibility =
                if (hasFocus && queryInput.text.isEmpty()) View.VISIBLE else View.GONE
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            searchHistoryFragment.visibility = View.GONE
            historyTracks.clear()
            rvHistoryTrack.adapter?.notifyDataSetChanged()
        }
    }

    override fun onStop() {
        super.onStop()
        searchHistory.saveHistory(historyTracks)
    }

    private fun search() {
        itunesService.search(queryInput.text.toString())
            .enqueue(object : Callback<TrackResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    Log.d("RESPONSE_CODE", "Status code: ${response.code()}")
                    Log.d("RESPONSE_BODY", "Status code: ${response.body()?.results}")
                    if (response.code() == SUCCESS) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
                            rvTrack.adapter?.notifyDataSetChanged()
                            setStatus(SearchStatus.SUCCESS)
                        }
                        if (tracks.isEmpty()) {
                            setStatus(SearchStatus.EMPTY_SEARCH)
                        }
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    setStatus(SearchStatus.CONNECTION_ERROR)
                }

            })
    }

    private fun setStatus(status: SearchStatus) {
        when (status) {
            SearchStatus.CONNECTION_ERROR -> {
                networkIssue.visibility = View.VISIBLE
                searchHistoryFragment.visibility = View.GONE
                rvTrack.visibility = View.GONE
                nothingFound.visibility = View.GONE
            }

            SearchStatus.EMPTY_SEARCH -> {
                nothingFound.visibility = View.VISIBLE
                searchHistoryFragment.visibility = View.GONE
                networkIssue.visibility = View.GONE
                rvTrack.visibility = View.GONE
            }

            SearchStatus.SUCCESS -> {
                rvTrack.visibility = View.VISIBLE
                searchHistoryFragment.visibility = View.GONE
                networkIssue.visibility = View.GONE
                nothingFound.visibility = View.GONE
            }

            SearchStatus.HISTORY -> {
                searchHistoryFragment.visibility = View.VISIBLE
                networkIssue.visibility = View.GONE
                nothingFound.visibility = View.GONE
                rvTrack.visibility = View.GONE
            }

            SearchStatus.ALL_GONE -> {
                searchHistoryFragment.visibility = View.GONE
                networkIssue.visibility = View.GONE
                nothingFound.visibility = View.GONE
                rvTrack.visibility = View.GONE
            }
        }
    }

    private fun addToRecentHistoryList(track: Track) {
        for (index in historyTracks.indices) {
            if (historyTracks[index].trackId == track.trackId) {
                historyTracks.removeAt(index)
                historyTracks.add(0, track)
                rvHistoryTrack.adapter?.notifyItemMoved(index, 0)
                return
            }
        }
        if (historyTracks.size < 10) {
            historyTracks.add(0, track)
            rvHistoryTrack.adapter?.notifyItemInserted(0)
            rvHistoryTrack.adapter?.notifyItemRangeChanged(
                0,
                historyTracks.size
            )
        } else {
            historyTracks.removeAt(9)
            rvHistoryTrack.adapter?.notifyItemRemoved(0)
            rvHistoryTrack.adapter?.notifyItemRangeChanged(
                9,
                historyTracks.size
            )
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val searchText = queryInput.text.toString()
        outState.putString(SEARCH_INPUT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val savedText = savedInstanceState.getString(SEARCH_INPUT)
        queryInput.setText(savedText)
    }

    companion object {
        const val SEARCH_INPUT = "SEARCH_INPUT"
    }

    enum class SearchStatus {
        CONNECTION_ERROR,
        EMPTY_SEARCH,
        SUCCESS,
        HISTORY,
        ALL_GONE
    }
}