package com.example.space_ship_game_2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.space_ship_game_2.databinding.ActivityRecordsBinding

class RecordsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_records)
        binding = ActivityRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        backToMenu()
        showBackground()
    }

    private fun initViews(){
        val mapsFragment = MapsFragment()
        supportFragmentManager.beginTransaction().replace(R.id.layMap, mapsFragment).commit()

        val recordListFragment = RecordListFragment()
        supportFragmentManager.beginTransaction().replace(R.id.layLst, recordListFragment).commit()

    }
    private fun showBackground(){
        val alienImageView = binding.bkRecords
        Glide.with(this)
            .load(R.drawable.bk_loop_space)
            .into(alienImageView)
    }


    private fun backToMenu(){
        binding.btnBackToMenu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}