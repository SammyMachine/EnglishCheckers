package com.example.englishcheckers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Draw : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_draw)
    }
    fun moveToStartMenu(view: View) {
        val drawIntent = Intent(this, MainActivity::class.java)
        startActivity(drawIntent)
    }
}