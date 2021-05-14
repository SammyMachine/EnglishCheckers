package com.example.englishcheckers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
    }
    fun moveToRules(view: View) {
        val rulesIntent = Intent(this, Rules::class.java)
        startActivity(rulesIntent)
    }
    fun finishApp(view: View) {
        finishAffinity()
    }
    fun moveToStart(view: View) {
        val startIntent = Intent(this, StartField::class.java)
        startActivity(startIntent)
    }
}