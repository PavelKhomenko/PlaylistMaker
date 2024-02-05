package com.example.playlistmaker.utils

import java.text.SimpleDateFormat
import java.util.Locale

class ConvertUlits {

    fun convertSizeToString(playlistSize: Int): String {
        val preLastDigit = playlistSize % 100
        if (preLastDigit in 5..20) return "$playlistSize треков"
        return when (preLastDigit % 10) {
            1 -> "$playlistSize трек"
            in(2..4) -> "$playlistSize трека"
            else -> "$playlistSize треков"
        }
    }

    fun conventDurationToMinutesString(playlistDuration: Int): String {
        val preLastDigit = playlistDuration % 100
        if (preLastDigit in 5..20) return "$playlistDuration минут"
        return when (preLastDigit % 10) {
            1 -> "$playlistDuration минута"
            in(2..4) -> "$playlistDuration минуты"
            else -> "$playlistDuration минут"
        }
    }

    fun formatTimeMillisToMinutesString(timeMillis: Long):Int =
        SimpleDateFormat("mm", Locale.getDefault()).format(timeMillis).toInt()
}