package com.example.coinTrade

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class LoadingActivity : AppCompatActivity() {

    private lateinit var loadingContainer: RelativeLayout
    private lateinit var bitcoinLogo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        loadingContainer = findViewById(R.id.loading_container)
        bitcoinLogo = findViewById(R.id.bitcoin_logo)

        startColorSwitchAnimation()

        // Delay for 3 seconds (3000 milliseconds) and then navigate to MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close the LoadingActivity so the user can't go back to it
        }, 5000) // 3 seconds delay
    }

    private fun startColorSwitchAnimation() {
        // Define colors
        val backgroundColorBlack = Color.BLACK
        val backgroundColorWhite = Color.WHITE

        // Animate background color
        val backgroundAnimator = ObjectAnimator.ofObject(
            loadingContainer,
            "backgroundColor",
            ArgbEvaluator(),
            backgroundColorBlack,
            backgroundColorWhite
        )
        backgroundAnimator.duration = 1000 // 1 second
        backgroundAnimator.repeatCount = ObjectAnimator.INFINITE
        backgroundAnimator.repeatMode = ObjectAnimator.REVERSE

        // Start animations
        backgroundAnimator.start()
    }
}