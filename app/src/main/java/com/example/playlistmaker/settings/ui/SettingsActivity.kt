package com.example.playlistmaker.settings.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.presentation.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private val back: View by lazy { findViewById(R.id.back) }
    private val themeSwitcher: SwitchMaterial by lazy { findViewById(R.id.themeSwitcher) }
    private val shareApp: ImageView by lazy { findViewById(R.id.share_app) }
    private val supportMail: ImageView by lazy { findViewById(R.id.support) }
    private val userAgreement: ImageView by lazy { findViewById(R.id.user_agreement) }

    private val viewModel by viewModels<SettingsViewModel> {
        SettingsViewModel.getViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        viewModel.observeThemeSwitcherState().observe(this) { isChecked ->
            themeSwitcher.isChecked = isChecked
        }

        setUpListeners()
    }

    private fun setUpListeners() {

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitcherClicked(isChecked)
        }

        shareApp.setOnClickListener {
            viewModel.onShareAppClicked()
        }

        supportMail.setOnClickListener {
            viewModel.onOpenSupportClicked()
        }

        userAgreement.setOnClickListener {
            viewModel.onOpenTermsClicked()
        }

        back.setOnClickListener {
            finish()
        }
    }
}