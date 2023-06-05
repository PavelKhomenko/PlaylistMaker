package com.example.playlistmaker


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val back = findViewById<View>(R.id.back)

        back.setOnClickListener {
            val displayMainMenuIntent = Intent(this, MainActivity::class.java)
            startActivity(displayMainMenuIntent)
        }
    }
}