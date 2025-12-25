package com.example.space_ship_game_2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.space_ship_game_2.databinding.ActivitySettingsBinding
import java.util.Locale

class SensorActivity : AppCompatActivity() {

//    private lateinit var binding: ActivitySettingsBinding
    private lateinit var accSensorApi: AccSensorApi

    private lateinit var sensorBundle : Bundle

    private val SENSOR_VALUES = "SENSOR_VALUES"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        binding = ActivitySettingsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        accSensorApi = AccSensorApi(this, object : AccSensorCallBack {
            override fun data(x: Float, y: Float, z: Float) {
                values(x, y, z)
            }
        })

    }


    private fun values(x: Float, y: Float, z: Float = 0f) {
        sensorBundle.putFloat(SENSOR_VALUES, x)
        sensorBundle.putFloat(SENSOR_VALUES, z)
        sensorBundle.putFloat(SENSOR_VALUES, y)
        intent.putExtra("SENSOR_BUNDLE", sensorBundle)
    }

    override fun onPause() {
        super.onPause()
        accSensorApi.stop()
    }

    override fun onResume() {
        super.onResume()
        accSensorApi.start()

    }
}