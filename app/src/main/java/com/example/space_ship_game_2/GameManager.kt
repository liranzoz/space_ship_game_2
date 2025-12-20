package com.example.space_ship_game_2.com.example.space_ship_game_2

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.collections.emptyList

object GameManager {
    private const val PREFS_NAME = "SpaceShipGamePrefs"
    private const val KEY_NICKNAME = "KEY_NICKNAME"
    private const val KEY_SCORES = "KEY_SCORES"

    var nickname: String = "Random Player"
        private set

    fun init(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        nickname = prefs.getString(KEY_NICKNAME, "Random Player") ?: "Random Player"
    }

    fun updateNickname(context: Context, newName: String) {
        nickname = newName
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_NICKNAME, newName).apply()
    }

    fun addScore(context: Context, score: Int, lat: Double, lon: Double) {
        val currentScores = getTopScores(context).toMutableList()
        currentScores.add(ScoreItem(nickname, score, lat, lon))

        currentScores.sortByDescending { it.score }

        val top10 = currentScores.take(10)
        saveScores(context, top10)
    }

    fun getTopScores(context: Context): List<ScoreItem> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_SCORES, null) ?: return emptyList()

        val gson = Gson()
        val type = object : TypeToken<List<ScoreItem>>() {}.type

        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveScores(context: Context, scores: List<ScoreItem>) {
        val gson = Gson()
        val jsonStr = gson.toJson(scores)

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_SCORES, jsonStr).apply()
    }
}