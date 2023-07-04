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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

    companion object {
        const val SEARCH_INPUT = "SEARCH_INPUT"
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

        adapter.tracks = tracks

        val rvTrack = findViewById<RecyclerView>(R.id.recyclerSearch)
        rvTrack.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvTrack.adapter = adapter

        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Log.d("TRANSLATION_LOG", "Status code: ${queryInput.text.toString()}")
                search()
                true
            }
            false
        }

    }

    private fun search() {
        val nothingFound = findViewById<View>(R.id.error_nothing_found)
        val networkIssue = findViewById<View>(R.id.error_no_connection)
        itunesService.search(queryInput.text.toString())
            .enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    Log.d("RESPONSE", "Status code: ${response.body()?.result}")
                    Log.d("RESPONSE", "Status code: ${response.code()}")
                    if (response.code() == 200) {
                        tracks.clear()
                        if (response.body()?.result?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.result!!)
                            adapter.notifyDataSetChanged()
                            nothingFound.visibility = View.GONE
                            networkIssue.visibility = View.GONE
                        }
                        if (tracks.isEmpty()) {
                            nothingFound.visibility = View.VISIBLE
                            networkIssue.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    networkIssue.visibility = View.VISIBLE
                    nothingFound.visibility = View.GONE
                }

            })
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