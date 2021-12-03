package com.internshala.foodering.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.internshala.foodering.R

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler().postDelayed({
            val startAct=Intent(this@SplashScreenActivity, LoginActivity::class.java)
            startActivity(startAct)
        },2000)

    }
    override fun onPause() {
        super.onPause()
        finish()
    }
}