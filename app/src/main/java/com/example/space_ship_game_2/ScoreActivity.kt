package com.example.space_ship_game_2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.space_ship_game_2.databinding.ActivityScoreBinding
import com.example.space_ship_game_2.databinding.ActivitySettingsBinding

class ScoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScoreBinding

    private var selectedGameMode: eGameMode = eGameMode.BUTTONS_MODE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showBackground()
        updateScore()
        handleClicks()
        showGif()
    }

    private fun showBackground() {
        val alienImageView = binding.bkScore
        Glide.with(this).load(R.drawable.bk_loop_space).into(alienImageView)
    }

    private fun updateScore(){
        val bundle = intent.getBundleExtra("SCORE_BUNDLE")
        val score = bundle?.getInt("SCORE_KEY")
        if (score != null) {
            if(score <= 50){
                binding.lblScore.setTextColor(Color.parseColor("#ff0000"))

            }
            else if(score > 150){
                binding.lblScore.setTextColor(Color.parseColor("#26DEC3"))
            }
        }
        binding.lblScore.text = score.toString()
    }

    private fun handleClicks(){
        binding.btnPlayAgain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("GAME_MODE_KEY", selectedGameMode)
            intent.putExtra("BUNDLE", bundle)
            startActivity(intent)
            finish()
        }
        binding.btnExit.setOnClickListener {finish()}
    }

    private fun showGif(){
        val alienImageView = binding.imgScoreGif
        Glide.with(this).asGif().load(R.drawable.gif_rick_score).override(600, 600).into(alienImageView)
    }

}