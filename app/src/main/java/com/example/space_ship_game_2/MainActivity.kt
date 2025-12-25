package com.example.space_ship_game_2

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import com.example.space_ship_game_2.com.example.space_ship_game_2.GameManager
import com.example.space_ship_game_2.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Random
import java.util.Timer
import java.util.TimerTask


class MainActivity : AppCompatActivity() {
    val ROWS = 5
    val COLS = 5
    var shipCol = 2

    val LASTROWINDEX = 4

    var SCOREACCLERATION = 10
    var delay: Long = 700
    lateinit var gameMatrix: Array<Array<ImageView?>?>
    private lateinit var binding: ActivityMainBinding
    var lives = 3
    var score = 0
    var currentCol: Int = -1
    var currentRow: Int = -1
    lateinit var gameTimer: Timer
    lateinit var random: Random
    var isGameRunning : Boolean = true

    var isGamePaused : Boolean = false

    private lateinit var accSensorApi: AccSensorApi

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // will be info popup
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val bundle = intent.getBundleExtra("BUNDLE")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermission()

        SoundManager.stopBackgroundMusic()
        SoundManager.startBackgroundMusic(R.raw.get_shwifty)

        showBackground()
        matrixInit()
        showPlayerAvatar()
        startFalling()//main func

        val gameMode = bundle?.getSerializable("GAME_MODE_KEY") as? eGameMode
        if (gameMode == eGameMode.SENSORS_MODE){
            hideButtons()
            accSensorApi = AccSensorApi(this, object : AccSensorCallBack {
                override fun data(x: Float, y: Float, z: Float) {
                    moveShipSensors(x)
                }
            })
        }
        else {
            movementButtons()
        }

        binding.btnPause.setOnClickListener {
            onPauseClicked()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::accSensorApi.isInitialized) {
            accSensorApi.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::accSensorApi.isInitialized) {
            accSensorApi.stop()
        }
    }


    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    private fun showBackground(){
        val alienImageView = binding.bkGame
        Glide.with(this).load(R.drawable.bk_loop_space).into(alienImageView)
    }
    private fun showPlayerAvatar(alienImageView: ImageView = binding.pickleRick42){
        Glide.with(this)
            .load(R.drawable.pickle_rick_sticker)
            .into(alienImageView)
    }

    private fun matrixInit(){
        gameMatrix = Array(ROWS) { arrayOfNulls(COLS) }
        gameMatrix[LASTROWINDEX-4]?.set(0, binding.solider00)
        gameMatrix[LASTROWINDEX-4]?.set(1, binding.solider01)
        gameMatrix[LASTROWINDEX-4]?.set(2, binding.solider02)
        gameMatrix[LASTROWINDEX-4]?.set(3, binding.solider03)
        gameMatrix[LASTROWINDEX-4]?.set(4, binding.solider04)

        gameMatrix[LASTROWINDEX-3]?.set(0, binding.solider10)
        gameMatrix[LASTROWINDEX-3]?.set(1, binding.solider11)
        gameMatrix[LASTROWINDEX-3]?.set(2, binding.solider12)
        gameMatrix[LASTROWINDEX-3]?.set(3, binding.solider13)
        gameMatrix[LASTROWINDEX-3]?.set(4, binding.solider14)

        gameMatrix[LASTROWINDEX-2]?.set(0, binding.solider20)
        gameMatrix[LASTROWINDEX-2]?.set(1, binding.solider21)
        gameMatrix[LASTROWINDEX-2]?.set(2, binding.solider22)
        gameMatrix[LASTROWINDEX-2]?.set(3, binding.solider24)
        gameMatrix[LASTROWINDEX-2]?.set(4, binding.solider25)

        gameMatrix[LASTROWINDEX-1]?.set(0, binding.solider30)
        gameMatrix[LASTROWINDEX-1]?.set(1, binding.solider31)
        gameMatrix[LASTROWINDEX-1]?.set(2, binding.solider32)
        gameMatrix[LASTROWINDEX-1]?.set(3, binding.solider33)
        gameMatrix[LASTROWINDEX-1]?.set(4, binding.solider34)

        gameMatrix[LASTROWINDEX]?.set(0, binding.pickleRick40)
        gameMatrix[LASTROWINDEX]?.set(1, binding.pickleRick41)
        gameMatrix[LASTROWINDEX]?.set(2, binding.pickleRick42)
        gameMatrix[LASTROWINDEX]?.set(3, binding.pickleRick43)
        gameMatrix[LASTROWINDEX]?.set(4, binding.pickleRick44)

        restartMatrix()
    }

    private fun startFalling() {
        random = Random()
        gameTimer = Timer()
        gameTimer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread(object : Runnable {
                    @RequiresPermission(Manifest.permission.VIBRATE)
                    override fun run() {
                        if (!isGameRunning) return
                        if (isGamePaused) return

                        for (col in 0 until COLS) {
                            if (col != shipCol && gameMatrix[LASTROWINDEX]?.get(col)?.visibility == View.VISIBLE) {
                                gameMatrix[LASTROWINDEX]?.get(col)?.visibility = View.INVISIBLE
                                score++
                                updateScoreUI()

                                if (score % SCOREACCLERATION == 0) {
                                    delay -= 50
                                    if (delay < 200) delay = 200
                                    gameTimer.cancel()
                                    startFalling()
                                    return
                                }
                            }
                        }

                        for (row in LASTROWINDEX - 1 downTo 0) {
                            for (col in 0 until COLS) {
                                if (gameMatrix[row]?.get(col)?.visibility == View.VISIBLE) {
                                    gameMatrix[row]?.get(col)?.visibility = View.INVISIBLE
                                    val nextRow = row + 1

                                    if (nextRow == LASTROWINDEX) { //hit
                                        if (col == shipCol) {
                                            lives--
                                            vibrate()
                                            SoundManager.playSound(R.raw.sound_eating_roar)
                                            updateLivesUI()
                                            if (lives == 0) { //gameover
                                                isGameRunning = false
                                                gameTimer.cancel()
                                                saveScore()
                                                handleGameOver()
                                                SoundManager.pauseBackgroundMusic()
                                                SoundManager.playSound(R.raw.sound_rick_sanchez_player)
                                                return
                                            }
                                        } else {
                                            gameMatrix[LASTROWINDEX]?.get(col)?.setImageResource(R.drawable.gromflomite_soldier)
                                            gameMatrix[LASTROWINDEX]?.get(col)?.visibility = View.VISIBLE
                                        }
                                    } else {
                                        gameMatrix[nextRow]?.get(col)?.setImageResource(R.drawable.gromflomite_soldier)
                                        gameMatrix[nextRow]?.get(col)?.visibility = View.VISIBLE
                                    }
                                }
                            }
                        }

                        val newCol = random.nextInt(COLS)
                        gameMatrix[0]?.get(newCol)?.setImageResource(R.drawable.gromflomite_soldier)
                        gameMatrix[0]?.get(newCol)?.visibility = View.VISIBLE
                    }
                })
            }
        }, 0, delay)
    }//main func

    private fun hideButtons(){
            binding.btnArrowLeft.isGone = true
            binding.btnArrowRight.isGone = true
    }

    private  fun movementButtons(){
        binding.btnArrowLeft.setOnClickListener {moveShipButtons(-1)}
        binding.btnArrowRight.setOnClickListener {moveShipButtons(1)}
    }

    private fun moveShipSensors(sensorTilt: Float) {
        var newCol = shipCol


        if (sensorTilt in 1f..3f) { //light left
            newCol--
        }
         else if (sensorTilt > 3f) { //strong left
            newCol -=2
        }
         else if (sensorTilt in -3f..-1f) { //light right
             newCol++
         }
         else if (sensorTilt < -3f) { //strong right
             newCol +=2
         }

        if (newCol < 0) {
            newCol = 0
        }
        if (newCol > 4) { // או COLS - 1
            newCol = 4
        }
        if (newCol != shipCol) {
            gameMatrix[LASTROWINDEX]?.get(shipCol)?.visibility = View.INVISIBLE
            shipCol = newCol
            updateSoliderPic(shipCol)
            gameMatrix[LASTROWINDEX]?.get(shipCol)?.visibility = View.VISIBLE
        }

    }

    private fun moveShipButtons(direction: Int) {

        val newCol = shipCol + direction

        if (newCol < 0 || newCol > LASTROWINDEX) {
            return
        }

        gameMatrix[LASTROWINDEX]?.get(shipCol)?.visibility = View.INVISIBLE
        shipCol = newCol
        updateSoliderPic(shipCol)
        gameMatrix[LASTROWINDEX]?.get(shipCol)?.visibility = View.VISIBLE

    }


    private fun updateSoliderPic(shipCol : Int) {
        if (shipCol == 0){showPlayerAvatar(binding.pickleRick40)}

        if (shipCol == 1){showPlayerAvatar(binding.pickleRick41)}

        if (shipCol == 2){showPlayerAvatar(binding.pickleRick42)}

        if (shipCol == 3){showPlayerAvatar(binding.pickleRick43)}

        if (shipCol == 4){showPlayerAvatar(binding.pickleRick44)}

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

    private fun handleGameOver(){
        val intent = Intent(this, ScoreActivity::class.java)
        val bundle = Bundle()
        val SCORE_KEY = "SCORE_KEY"
        bundle.putInt(SCORE_KEY, score)
        intent.putExtra("SCORE_BUNDLE", bundle)
        SoundManager.stopBackgroundMusic()
        SoundManager.startBackgroundMusic(R.raw.snd_rick_and_morty_theme_song)
        startActivity(intent)
        finish()
    }

    private fun showGameOverDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("wabbalabbadubdub!")
        builder.setMessage("Game Over! What would you like to do?")
        builder.setCancelable(false)

        builder.setPositiveButton("Restart") { _, _ ->
            restartGame()
        }

        builder.setNegativeButton("Exit") { _, _ ->


        }
        builder.show()
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
                if (imageView != gameMatrix[LASTROWINDEX]?.get(shipCol)) {
                    imageView?.visibility = View.INVISIBLE
                }
            }
        }

    }

    private fun updateScoreUI(){
        binding.scoreCounter.text = score.toString()
    }

    private fun saveScore(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    GameManager.addScore(this, score, location.latitude, location.longitude)
                } else {
                    GameManager.addScore(this, score, 0.0, 0.0)
                }

            }
        } else {
            GameManager.addScore(this, score, 0.0, 0.0)
        }
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(150)
        }

    }

    private fun onPauseClicked() {
        isGamePaused = true
        SoundManager.toggleSound(false)
        showPauseDialog()
    }

    private fun showPauseDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("PAUSED")
        builder.setMessage("Game is paused")
        builder.setCancelable(false)
        builder.setPositiveButton("Resume") { dialog, _ ->
            isGamePaused = false
            SoundManager.toggleSound(true)
            dialog.dismiss()
        }
        builder.setNegativeButton("Quit") { _, _ ->
            finish()
        }
        builder.create().show()
    }


}




