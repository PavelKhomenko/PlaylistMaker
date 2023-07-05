package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    private lateinit var editText: EditText

    companion object {
        const val SEARCH_INPUT = "SEARCH_INPUT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val back = findViewById<View>(R.id.back).apply {
            setOnClickListener {
                finish()
            }
        }

        val clearButton = findViewById<ImageView>(R.id.clear_text).apply {
            setOnClickListener {
                editText.text = null
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
            }
        }

        editText = findViewById<EditText?>(R.id.search_input).apply { requestFocus() }
        editText.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                clearButton.visibility = View.INVISIBLE
            } else {
                clearButton.visibility = View.VISIBLE
            }
        }

        val trackAdapter = TrackAdapter(
            listOf(
                Track("Octane",
                    "Until I Wake",
                    "3:29",
                    ""
                ),
                Track(
                    "Smells Like Teen Spirit",
                    "Nirvana",
                    "5:01",
                    "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
                ),
                Track("Year Zero",
                    "Ghost",
                    "5:50",
                    ""
                ),
                Track(
                    "Billie Jean",
                    "Michael Jackson",
                    "4:35",
                    "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
                ),
                Track(
                    "OhNo!",
                    "BONES",
                    "2:39",
                    ""
                ),
                Track(
                    "Stayin' Alive",
                    "Bee Gees",
                    "4:10",
                    "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
                ),
                Track(
                    "Whole Lotta Love",
                    "Led Zeppelin",
                    "5:33",
                    "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
                ),
                Track(
                    "Psycho",
                    "Puddle of Mudd",
                    "3:30",
                    ""
                ),
                Track(
                    "Sweet Child O'Mine",
                    "Guns N' Roses",
                    "5:03",
                    "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
                ),
                Track(
                    "Run",
                    "Zeal & Ardor",
                    "3:16",
                    ""
                ),
            )
        )
        val rvTrack = findViewById<RecyclerView>(R.id.recyclerSearch)
        rvTrack.adapter = trackAdapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val searchText = editText.text.toString()
        outState.putString(SEARCH_INPUT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val savedText = savedInstanceState.getString(SEARCH_INPUT)
        editText.setText(savedText)
    }
}