package com.example.englishcheckers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class FinishedGameWhite : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_finished_game_white)
    }

    fun moveToMainMenu(view: View) {
        val finishIntent = Intent(this, MainActivity::class.java)
        startActivity(finishIntent)
    }
}