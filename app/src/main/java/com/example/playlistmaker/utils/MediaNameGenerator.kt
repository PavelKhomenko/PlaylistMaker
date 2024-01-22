package com.example.playlistmaker.utils

object MediaNameGenerator {
    fun generateName(): String {
        val chars = ('A'..'Z') + ('a'..'z')
        return (6..8)
            .map { chars.random() }
            .joinToString("")
            .plus(".jpg")
    }
}