package com.example.playlistmaker.main


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.SettingsActivity
import com.example.playlistmaker.library.LibraryActivity
import com.example.playlistmaker.search.SearchActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val search = findViewById<Button>(R.id.search)
        search.setOnClickListener {
            val displaySearchIntent = Intent(this, SearchActivity::class.java)
            startActivity(displaySearchIntent)
        }

        val library = findViewById<Button>(R.id.library)
        library.setOnClickListener {
            val displayLibraryIntent = Intent(this, LibraryActivity::class.java)
            startActivity(displayLibraryIntent)
        }

        val settings = findViewById<Button>(R.id.settings)
        settings.setOnClickListener {
            val displaySettingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displaySettingsIntent)
        }

    }
}