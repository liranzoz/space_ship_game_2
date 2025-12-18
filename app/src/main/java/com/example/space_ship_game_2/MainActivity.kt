package com.example.space_ship_game_2

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.space_ship_game_2.SoundManager
import com.example.space_ship_game_2.databinding.ActivityMainBinding
import java.util.Random
import java.util.Timer
import java.util.TimerTask


class MainActivity : AppCompatActivity() {
    val ROWS = 5
    val COLS = 3
    var shipCol = 1
    lateinit var gameMatrix: Array<Array<ImageView?>?>
    private lateinit var binding: ActivityMainBinding
    var lives = 3
    var score = 0
    var currentCol: Int = -1
    var currentRow: Int = -1
    lateinit var gameTimer: Timer
    lateinit var random: Random
    var isGameRunning : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SoundManager.stopBackgroundMusic()
        SoundManager.startBackgroundMusic(R.raw.get_shwifty)
        showBackground()
        matrixInit()
        showPlayerAvatar()
        startFalling()
        movementButtons()

    }

    private fun showBackground(){
        val alienImageView = binding.bkGame
        Glide.with(this).load(R.drawable.bk_loop_space).into(alienImageView)
    }
    private fun showPlayerAvatar(alienImageView: ImageView = binding.soliderShip41){
        Glide.with(this)
            .load(R.drawable.pickle_rick_sticker)
            .into(alienImageView)
    }

    private fun matrixInit(){
        gameMatrix = Array<Array<ImageView?>?>(ROWS) { arrayOfNulls<ImageView>(COLS) }
        gameMatrix[0]?.set(0, binding.solider00);
        gameMatrix[0]?.set(1, binding.solider01);
        gameMatrix[0]?.set(2, binding.solider02);
        gameMatrix[1]?.set(0, binding.solider10);
        gameMatrix[1]?.set(1, binding.solider11);
        gameMatrix[1]?.set(2, binding.solider12);
        gameMatrix[2]?.set(0, binding.solider20);
        gameMatrix[2]?.set(1, binding.solider21Center);
        gameMatrix[2]?.set(2, binding.solider22);
        gameMatrix[3]?.set(0, binding.solider30);
        gameMatrix[3]?.set(1, binding.solider31);
        gameMatrix[3]?.set(2, binding.solider32);
        gameMatrix[4]?.set(0, binding.soliderShip40);
        gameMatrix[4]?.set(1, binding.soliderShip41);
        gameMatrix[4]?.set(2, binding.soliderShip42);

        restartMatrix()
    }

    private fun startFalling() {
        random = Random()
        currentCol = random.nextInt(3)
        currentRow = 0

        gameTimer = Timer()
        gameTimer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread(object : Runnable {
                    @RequiresPermission(Manifest.permission.VIBRATE)
                    override fun run() {
                        updateAlienPosition()
                    }

                    @RequiresPermission(Manifest.permission.VIBRATE)
                    private fun updateAlienPosition() {
                        var prevRow : Int = 0
                             if (currentRow > 0) {
                                prevRow = currentRow - 1
                        }

                        if (prevRow != 4 || currentCol != shipCol) {
                            gameMatrix[prevRow]?.get(currentCol)?.visibility = View.INVISIBLE
                        }

                        if (currentRow == 4 && currentCol == shipCol){ // hit
                            lives--
                            vibrate()
                            updateLivesUI()
                            gameTimer.cancel()
                            isGameRunning = false
                            gameMatrix[currentRow]?.get(currentCol)?.setImageResource(R.drawable.pickle_rick_sticker)
                            gameMatrix[currentRow]?.get(currentCol)?.visibility = View.VISIBLE
                            if (isGameEnded()){
                                showGameOverDialog()
                                SoundManager.pauseBackgroundMusic()
                                SoundManager.playSound(R.raw.wubalubadubdub)
                            }else{
                                startFalling()
                            }
                            return
                        }

                        if (currentRow >= 5) { //not hit
                            score++
                            updateScoreUI()
                            gameTimer.cancel()
                            startFalling()
                            return
                        }

                        //gameMatrix[currentRow]?.get(currentCol)?.setVisibility(View.VISIBLE)
                        val currentCell = gameMatrix[currentRow]?.get(currentCol)

                        if (currentRow == 4 && currentCol == shipCol){
                            currentCell?.setImageResource(R.drawable.pickle_rick_sticker)
                        }else{
                            currentCell?.setImageResource(R.drawable.gromflomite_soldier)
                        }


                        currentCell?.setVisibility(View.VISIBLE)
                        currentRow++

                    }
                })
            }
        }, 0, 700)
    }

    private  fun movementButtons(){
        binding.btnArrowLeft.setOnClickListener {moveShip(-1)}
        binding.btnArrowRight.setOnClickListener {moveShip(1)}
    }

    private fun moveShip(direction: Int) {
        val newCol = shipCol + direction

        if (newCol < 0 || newCol > 2) {
            return
        }

        gameMatrix[4]?.get(shipCol)?.visibility = View.INVISIBLE
        shipCol = newCol
        updateSoliderPic(shipCol)
        gameMatrix[4]?.get(shipCol)?.visibility = View.VISIBLE

    }

    private fun updateSoliderPic(shipCol : Int) {
        if (shipCol == 0){
            showPlayerAvatar(binding.soliderShip40)
//           .setImageResource(R.drawable.pickle_rick_sticker)
        }
        if (shipCol == 1){
            showPlayerAvatar(binding.soliderShip41)
//           .setImageResource(R.drawable.pickle_rick_sticker)
        }
        if (shipCol == 2){
            showPlayerAvatar(binding.soliderShip42)
//           .setImageResource(R.drawable.pickle_rick_sticker)
        }
    }

    private fun updateLivesUI() {
        if (lives == 3){
            binding.icHeart3.visibility = View.VISIBLE
            binding.icHeart2.visibility = View.VISIBLE
            binding.icHeart1.visibility = View.VISIBLE
        }

        if (lives < 3) binding.icHeart3.visibility = View.INVISIBLE
        if (lives < 2) binding.icHeart2.visibility = View.INVISIBLE
        if (lives < 1) binding.icHeart1.visibility = View.INVISIBLE
    }

    private fun showGameOverDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("wabbalabbadubdub!")
        builder.setMessage("Game Over! What would you like to do?")
        builder.setCancelable(false)

        builder.setPositiveButton("Restart") { _, _ ->
            restartGame()
        }

        builder.setNegativeButton("Exit to menu") { _, _ ->
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            SoundManager.stopBackgroundMusic()
            SoundManager.startBackgroundMusic(R.raw.snd_rick_and_morty_theme_song)
        }
        builder.show()
    }

    private fun isGameEnded() : Boolean{
        if (!isGameRunning && lives == 0){
            return true
        }
        return false
    }

    private fun restartGame() {
        lives = 3
        score = 0
        updateScoreUI()
        updateLivesUI()
        restartMatrix()
        isGameRunning = true
        startFalling()
    }

    private fun restartMatrix(){
        for (row in gameMatrix) {
            row?.forEach { imageView ->
                if (imageView != gameMatrix[4]?.get(shipCol)) {
                    imageView?.visibility = View.INVISIBLE
                }
            }
        }

    }

    private fun updateScoreUI(){
        binding.scoreCounter.text = score.toString()
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= 26) {
            (this.getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            (this.getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(150)
        }
    }
}


