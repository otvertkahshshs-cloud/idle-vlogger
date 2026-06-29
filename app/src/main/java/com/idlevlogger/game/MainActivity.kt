package com.idlevlogger.game

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

        binding.btnRecord.setOnClickListener {
            GameManager.recordVideo()
            updateUI()
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
        binding.tvVideos.text = "🎬 Видео: ${GameManager.formatNumber(s.videoCount)}"
        binding.tvPerClick.text = "+${GameManager.formatNumber(s.moneyPerClick)} за видео"
        binding.tvPerSec.text = if (s.moneyPerSec > 0)
            "+${GameManager.formatNumber(s.moneyPerSec)}/сек" else "Нет авто-дохода"
    }
}
