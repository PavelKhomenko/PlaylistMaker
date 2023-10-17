package com.example.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.models.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    private val emailSubject = context.getString(R.string.email_subject)
    private val emailBody = context.getString(R.string.email_body)
    private val chooser = context.getString(R.string.select)

    override fun shareLink(shareAppLink: String) {
        val shareIntentLink = Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, shareAppLink)
            type = "text/html"
        }, chooser)
        context.startActivity(shareIntentLink.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openLink(termsLink: String) {
        val userAgreement = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(termsLink)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(userAgreement)
    }

    override fun openEmail(supportEmail: EmailData) {
        val shareIntentEmail = Intent.createChooser(Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(supportEmail.mailTo)
            putExtra(Intent.EXTRA_EMAIL, supportEmail.mail)
            putExtra(Intent.EXTRA_SUBJECT, emailSubject)
            putExtra(Intent.EXTRA_TEXT, emailBody)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }, chooser)
        context.startActivity(Intent(shareIntentEmail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)))
    }
}