package com.example.space_ship_game_2

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.space_ship_game_2.com.example.space_ship_game_2.GameManager
import com.example.space_ship_game_2.databinding.ActivitySettingsBinding



class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    val GAME_MODE_KEY = "GAME_MODE_KEY"
    private lateinit var bundle: Bundle
    private lateinit var gameMode : eGameMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showBackground()
        initViews()
        handleClicks()
    }

    private fun handleClicks() {
        binding.btnBackToMenu.setOnClickListener {
            backToMenu()
        }

        binding.cardNickname.setOnClickListener {
            showEditNicknameDialog()
        }
    }

    private fun initViews() {
        binding.txtNickName.text = GameManager.nickname
    }

    private fun showBackground() {
        val alienImageView = binding.bkSettings
        Glide.with(this).load(R.drawable.bk_loop_space).into(alienImageView)
    }

    private fun backToMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        val bundle = Bundle()
        val mode = if (binding.switchMode.isChecked) {
            eGameMode.SENSORS_MODE
        } else {
            eGameMode.BUTTONS_MODE
        }
        bundle.putSerializable("GAME_MODE_KEY", mode)
        intent.putExtra("BUNDLE", bundle)

        startActivity(intent)
        finish()
    }

    private fun showEditNicknameDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Nickname")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setText(GameManager.nickname)
        builder.setView(input)

        builder.setPositiveButton("Save") { dialog, _ ->
            val newName = input.text.toString()
            if (newName.isNotEmpty()) {
                saveNewNickname(newName)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
    private fun saveNewNickname(newName: String) {
        GameManager.updateNickname(this, newName)
        binding.txtNickName.text = newName
    }

    private fun gameMode(){
        val switchBtn = binding.switchMode
        bundle = Bundle()

        gameMode = if (switchBtn.isChecked){
            eGameMode.SENSORS_MODE
        } else{
            eGameMode.BUTTONS_MODE
        }
        bundle.putSerializable(GAME_MODE_KEY,gameMode)
        intent.putExtra("BUNDLE", bundle)
    }
}
