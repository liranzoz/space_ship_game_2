import android.content.Context
import android.content.SharedPreferences

object GameManager {
    private const val PREFS_NAME = "SpaceShipGamePrefs"
    private const val KEY_NICKNAME = "KEY_NICKNAME"

    var nickname: String = "player"
        private set

    fun init(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        nickname = prefs.getString(KEY_NICKNAME, "player") ?: "player"
    }

    fun updateNickname(context: Context, newName: String) {
        nickname = newName
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_NICKNAME, newName).apply()
    }
}