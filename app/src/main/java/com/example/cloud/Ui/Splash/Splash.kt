package com.example.cloud.Ui.Splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.cloud.Ui.Main.MainActivity
import com.example.cloud.databinding.ActivitySplashBinding

class Splash : AppCompatActivity() {
    private val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        Handler(Looper.getMainLooper()).postDelayed({
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }, 5000)
        yoyo()


    }

    private fun yoyo(){
        YoYo.with(Techniques.FadeIn).duration(2000).playOn(binding.imageSplash)
        YoYo.with(Techniques.FadeIn).duration(3000).playOn(binding.textSplash)
    }
}