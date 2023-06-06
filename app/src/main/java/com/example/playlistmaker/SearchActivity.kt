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

class SearchActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    companion object {
        const val SEARCH_INPUT = "SEARCH_INPUT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        editText = findViewById(R.id.search_input)
        if (savedInstanceState != null) {
            val savedText = savedInstanceState.getString(SEARCH_INPUT)
            editText.setText(savedText)
        }
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        editText.requestFocus()
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)

        val clearButton = findViewById<ImageView>(R.id.clear_text)

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.isNullOrEmpty()) {
                    clearButton.visibility = View.INVISIBLE
                } else {
                    clearButton.visibility = View.VISIBLE
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })

        clearButton.setOnClickListener {
            editText.text = null
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
            clearButton.requestFocus()
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