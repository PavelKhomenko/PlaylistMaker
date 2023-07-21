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
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val SUCCESS = 200
class SearchActivity : AppCompatActivity() {

    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ItunesApi::class.java)

    private val tracks = ArrayList<Track>()

    private val adapter = TrackAdapter()

    private lateinit var queryInput: EditText
    private lateinit var back: View
    private lateinit var clearButton: ImageView
    private lateinit var rvTrack: RecyclerView

    companion object {
        const val SEARCH_INPUT = "SEARCH_INPUT"
    }

    enum class SearchStatus {
        CONNECTION_ERROR,
        EMPTY_SEARCH,
        SUCCESS
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        back = findViewById<View>(R.id.back).apply {
            setOnClickListener {
                finish()
            }
        }

        clearButton = findViewById<ImageView>(R.id.clear_text).apply {
            setOnClickListener {
                queryInput.text = null
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(queryInput.windowToken, 0)
            }
        }

        queryInput = findViewById<EditText?>(R.id.search_input).apply { requestFocus() }
        queryInput.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                clearButton.visibility = View.INVISIBLE
            } else {
                clearButton.visibility = View.VISIBLE
            }
        }

        val refresh = findViewById<Button>(R.id.refreshButton).apply {
            setOnClickListener { search() }
        }

        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
            }
            false
        }

        adapter.tracks = tracks
        rvTrack = findViewById(R.id.recyclerSearch)
        rvTrack.adapter = adapter
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
                            adapter.notifyDataSetChanged()
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
        val nothingFound = findViewById<View>(R.id.error_nothing_found)
        val networkIssue = findViewById<View>(R.id.error_no_connection)
        when (status) {
            SearchStatus.CONNECTION_ERROR -> {
                networkIssue.visibility = View.VISIBLE
                rvTrack.visibility = View.GONE
                nothingFound.visibility = View.GONE
            }
            SearchStatus.EMPTY_SEARCH -> {
                nothingFound.visibility = View.VISIBLE
                networkIssue.visibility = View.GONE
                rvTrack.visibility = View.GONE
            }
            SearchStatus.SUCCESS -> {
                rvTrack.visibility = View.VISIBLE
                networkIssue.visibility = View.GONE
                nothingFound.visibility = View.GONE
            }
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
}