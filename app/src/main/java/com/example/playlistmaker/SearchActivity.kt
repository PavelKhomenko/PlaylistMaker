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