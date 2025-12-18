package com.example.space_ship_game_2

import android.content.Context
import android.media.MediaPlayer

object SoundManager {

    private var backgroundMediaPlayer: MediaPlayer? = null
    private lateinit var appContext: Context
    private var isMusicEnabled = true

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun playSound(resId: Int) {
        if (!isMusicEnabled) return
        val tempMediaPlayer = MediaPlayer.create(appContext, resId)
        tempMediaPlayer.setOnCompletionListener { mp -> mp.release() }
        tempMediaPlayer.start()
    }

    fun startBackgroundMusic(resId: Int) {
        if (backgroundMediaPlayer != null && backgroundMediaPlayer!!.isPlaying) return

        stopBackgroundMusic()

        backgroundMediaPlayer = MediaPlayer.create(appContext, resId)
        backgroundMediaPlayer?.isLooping = true
        backgroundMediaPlayer?.setVolume(1f, 1f)

        if (isMusicEnabled) {
            backgroundMediaPlayer?.start()
        }
    }

    fun pauseBackgroundMusic() {
        if (backgroundMediaPlayer != null && backgroundMediaPlayer!!.isPlaying) {
            backgroundMediaPlayer?.pause()
        }
    }

    fun resumeBackgroundMusic() {
        if (backgroundMediaPlayer != null && !backgroundMediaPlayer!!.isPlaying && isMusicEnabled) {
            backgroundMediaPlayer?.start()
        }
    }

    fun stopBackgroundMusic() {
        backgroundMediaPlayer?.stop()
        backgroundMediaPlayer?.release()
        backgroundMediaPlayer = null
    }

    fun toggleSound(enable: Boolean) {
        isMusicEnabled = enable
        if (!enable) {
            pauseBackgroundMusic()
        } else {
            resumeBackgroundMusic()
        }
    }
}