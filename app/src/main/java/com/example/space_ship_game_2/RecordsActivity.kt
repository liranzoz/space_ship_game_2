package com.example.space_ship_game_2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.space_ship_game_2.databinding.ActivityRecordsBinding

class RecordsActivity : AppCompatActivity(), OnScoreItemClickListener {

    private lateinit var binding: ActivityRecordsBinding
    val mapsFragment = MapsFragment()
    val recordListFragment = RecordListFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_records)
        binding = ActivityRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        initFragments()
        backToMenu()
        showBackground()
    }

    private fun initViews(){
        supportFragmentManager.beginTransaction().replace(R.id.layMap, mapsFragment).commit()
        supportFragmentManager.beginTransaction().replace(R.id.layLst, recordListFragment).commit()

    }
    private fun initFragments() {
        recordListFragment.itemClickListener = this
        supportFragmentManager.beginTransaction().replace(R.id.layMap, mapsFragment).replace(R.id.layLst, recordListFragment).commit()
    }

    override fun onScoreItemClicked(lat: Double, lon: Double) {
        mapsFragment.zoomToLocation(lat, lon)
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