package com.example.android.actionopendocument

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.login_button).setOnClickListener {
            val userName = findViewById<EditText>(R.id.username_input).text.toString()
            if (userName.isNotBlank()) {
                saveLoginInfo(userName)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun saveLoginInfo(userName: String) {
        val sharedPrefs = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
        val currentTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        // Recuperar historial existente
        val history = sharedPrefs.getStringSet(KEY_HISTORY, mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        // Agregar nuevo registro (formato: usuario|fecha)
        history.add("$userName|$currentTime")

        // Guardar historial actualizado
        val editor = sharedPrefs.edit()
        editor.putStringSet(KEY_HISTORY, history)
        editor.apply()

        // Guardar también el último usuario para el flujo principal
        editor.putString(KEY_LAST_USER, userName)
        editor.apply()
    }

    companion object {
        const val USER_PREFS = "user_preferences"
        const val KEY_USERNAME = "username"
        const val KEY_LAST_USER = "last_user"
        const val KEY_HISTORY = "login_history"
    }
}
