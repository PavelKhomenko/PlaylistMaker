package com.example.playlistmaker.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class ToolsRouter {

    fun glideProvider(context: Context, url: String, placeholder: Int, cornerRadius: Int, view: ImageView) {
        Glide
            .with(context)
            .load(url.replaceAfterLast(DELIMITER, REPLACEMENTS))
            .placeholder(placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadius))
            .into(view)
    }

    companion object{
        private const val DELIMITER = '/'
        private const val REPLACEMENTS = "512x512bb.jpg"
    }
}