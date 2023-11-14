package com.example.playlistmaker.library.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.example.playlistmaker.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class LibraryActivity : AppCompatActivity() {

    private lateinit var tabMediator: TabLayoutMediator
    private val libraryViewPager: ViewPager2 by lazy { findViewById(R.id.viewPager) }
    private val libraryTabLayout: TabLayout by lazy { findViewById(R.id.tabLayout) }
    private val vgBack: View by lazy { findViewById(R.id.back) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
        libraryViewPager.adapter = LibraryPagerAdapter(supportFragmentManager, lifecycle)
        tabMediator = TabLayoutMediator(libraryTabLayout, libraryViewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.favorite_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabMediator.attach()
        vgBack.setOnClickListener { finish() }
    }
}