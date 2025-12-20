package com.example.space_ship_game_2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.space_ship_game_2.com.example.space_ship_game_2.GameManager
import com.example.space_ship_game_2.databinding.MainMenuBinding


class MenuActivity : AppCompatActivity() {

    private lateinit var binding: MainMenuBinding
    private var isSoundOn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main_menu)
        binding = MainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        GameManager.init(this)
        SoundManager.init(this)
        SoundManager.startBackgroundMusic(R.raw.snd_rick_and_morty_theme_song)
        showBackground()
        muteUnmuteButton()
        startGame()
        showRecords()
        showSettings()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        binding.txtNicknameView.text = GameManager.nickname
    }

    private fun showSettings() {
        binding.btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showBackground(){
        val alienImageView = binding.bkMainMenu
        Glide.with(this).load(R.drawable.bk_loop_space).into(alienImageView)
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

    private fun muteUnmuteButton(){
        binding.icBtnMuteUnmute.setOnClickListener {
            isSoundOn = !isSoundOn
            SoundManager.toggleSound(isSoundOn)
            if (isSoundOn) {
                binding.icBtnMuteUnmute.setImageResource(R.drawable.ic_unmute)
            } else {
                binding.icBtnMuteUnmute.setImageResource(R.drawable.ic_mute)
            }
        }

    }
}

