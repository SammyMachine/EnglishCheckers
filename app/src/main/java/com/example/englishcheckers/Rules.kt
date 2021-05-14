package com.example.englishcheckers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Rules : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_rules)
    }
    fun moveToStart(view: View) {
        val rulesIntent = Intent(this, MainActivity::class.java)
        startActivity(rulesIntent)
    }
}