package com.example.space_ship_game_2

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.space_ship_game_2.MainActivity
import com.example.space_ship_game_2.RecordsActivity
import com.example.space_ship_game_2.SoundManager
import com.example.space_ship_game_2.databinding.ActivityMainBinding
import com.example.space_ship_game_2.databinding.MainMenuBinding


class MenuActivity : AppCompatActivity() {

    private lateinit var binding: MainMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main_menu)
        binding = MainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SoundManager.init(this)
        SoundManager.startBackgroundMusic(R.raw.snd_rick_and_morty_theme_song)
        showBackground()
        startGame()
        showRecords()
    }


    private fun showBackground(){
        val alienImageView = binding.bkMainMenu
        Glide.with(this)
            .load(R.drawable.bk_loop_space)
            .into(alienImageView)
    }

    private fun startGame(){
        binding.btnStartGame.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showRecords(){
        binding.btnRecords.setOnClickListener {
            val intent = Intent(this, RecordsActivity::class.java)
            startActivity(intent)
        }
    }
}

