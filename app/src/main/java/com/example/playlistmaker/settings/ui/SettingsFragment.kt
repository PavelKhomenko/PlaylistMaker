package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.presentation.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val themeSwitcher: SwitchMaterial by lazy {  requireView().findViewById(R.id.themeSwitcher) }
    private val shareApp: ImageView by lazy {  requireView().findViewById(R.id.share_app) }
    private val supportMail: ImageView by lazy {  requireView().findViewById(R.id.support) }
    private val userAgreement: ImageView by lazy {  requireView().findViewById(R.id.user_agreement) }

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeThemeSwitcherState().observe(viewLifecycleOwner) { isChecked ->
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
    }
}