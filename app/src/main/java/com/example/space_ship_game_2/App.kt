package com.example.space_ship_game_2

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

class App : Application(), DefaultLifecycleObserver {

    override fun onCreate() {
        super<Application>.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        SoundManager.toggleSound(true)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        SoundManager.toggleSound(false)
    }
}