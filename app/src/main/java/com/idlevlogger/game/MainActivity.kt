package com.idlevlogger.game

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.idlevlogger.game.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val handler = Handler(Looper.getMainLooper())
    private val tickRunnable = object : Runnable {
        override fun run() {
            GameManager.tick()
            updateUI()
            GameManager.save()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GameManager.init(this)

        val bounceAnim = AnimationUtils.loadAnimation(this, R.anim.btn_bounce)
        val floatAnim = AnimationUtils.loadAnimation(this, R.anim.float_up)

        binding.btnRecord.setOnClickListener {
            GameManager.recordVideo()
            updateUI()

            // Bounce animation on button
            binding.btnRecord.startAnimation(bounceAnim)

            // Floating +money label
            val earned = GameManager.state.moneyPerClick
            binding.tvFloatMoney.text = "+${GameManager.formatNumber(earned)} 💰"
            binding.tvFloatMoney.visibility = View.VISIBLE
            floatAnim.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(a: android.view.animation.Animation?) {}
                override fun onAnimationRepeat(a: android.view.animation.Animation?) {}
                override fun onAnimationEnd(a: android.view.animation.Animation?) {
                    binding.tvFloatMoney.visibility = View.GONE
                }
            })
            binding.tvFloatMoney.startAnimation(floatAnim)
        }

        binding.btnShop.setOnClickListener {
            startActivity(Intent(this, ShopActivity::class.java))
        }

        updateUI()
    }

    override fun onResume() {
        super.onResume()
        handler.post(tickRunnable)
        updateUI()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(tickRunnable)
        GameManager.save()
    }

    private fun updateUI() {
        val s = GameManager.state
        binding.tvMoney.text = "💰 ${GameManager.formatNumber(s.money)}"
        binding.tvSubscribers.text = "👥 ${GameManager.formatNumber(s.subscribers)}"
        binding.tvVideos.text = "🎬 ${GameManager.formatNumber(s.videoCount)} видео"
        binding.tvPerClick.text = "+${GameManager.formatNumber(s.moneyPerClick)} за видео"
        binding.tvPerSec.text = if (s.moneyPerSec > 0)
            "+${GameManager.formatNumber(s.moneyPerSec)}/сек" else "Нет авто-дохода"
    }
}
