package com.idlevlogger.game

import android.content.Context
import android.content.SharedPreferences

object GameManager {

    private lateinit var prefs: SharedPreferences
    var state = GameState()

    // Upgrade costs
    val upgrades = listOf(
        Upgrade("Камера",       "Больше денег за видео",   "camera",   baseCost = 100L),
        Upgrade("Микрофон",     "Больше подписчиков",      "mic",      baseCost = 150L),
        Upgrade("Свет",         "Авто-доход/сек",          "light",    baseCost = 200L),
        Upgrade("Монтаж",       "Быстрее зарабатывать",    "editor",   baseCost = 500L),
        Upgrade("Продвижение",  "Авто-подписчики/сек",     "promo",    baseCost = 1000L)
    )

    fun init(context: Context) {
        prefs = context.getSharedPreferences("idle_vlogger", Context.MODE_PRIVATE)
        load()
    }

    fun recordVideo() {
        state.money += state.moneyPerClick
        state.videoCount++
        state.subscribers += state.subscribersPerVideo
    }

    fun tick() {
        state.money += state.moneyPerSec
        if (state.promoLevel > 0) {
            state.subscribers += state.promoLevel.toLong()
        }
    }

    fun getCost(upgradeId: String): Long {
        val upgrade = upgrades.first { it.id == upgradeId }
        val level = getLevel(upgradeId)
        return (upgrade.baseCost * Math.pow(1.5, level.toDouble())).toLong()
    }

    fun canAfford(upgradeId: String) = state.money >= getCost(upgradeId)

    fun buyUpgrade(upgradeId: String): Boolean {
        val cost = getCost(upgradeId)
        if (state.money < cost) return false
        state.money -= cost
        when (upgradeId) {
            "camera" -> {
                state.cameraLevel++
                state.moneyPerClick = 10L * state.cameraLevel
            }
            "mic" -> {
                state.micLevel++
                state.subscribersPerVideo = state.micLevel.toLong()
            }
            "light" -> {
                state.lightLevel++
                state.moneyPerSec = 5L * state.lightLevel
            }
            "editor" -> {
                state.editorLevel++
                state.moneyPerClick = (state.moneyPerClick * 1.2).toLong()
            }
            "promo" -> {
                state.promoLevel++
            }
        }
        save()
        return true
    }

    fun getLevel(upgradeId: String): Int = when (upgradeId) {
        "camera" -> state.cameraLevel
        "mic"    -> state.micLevel
        "light"  -> state.lightLevel
        "editor" -> state.editorLevel
        "promo"  -> state.promoLevel
        else     -> 1
    }

    fun save() {
        prefs.edit().apply {
            putLong("money", state.money)
            putLong("subscribers", state.subscribers)
            putLong("videoCount", state.videoCount)
            putLong("moneyPerClick", state.moneyPerClick)
            putLong("moneyPerSec", state.moneyPerSec)
            putLong("subscribersPerVideo", state.subscribersPerVideo)
            putInt("cameraLevel", state.cameraLevel)
            putInt("micLevel", state.micLevel)
            putInt("lightLevel", state.lightLevel)
            putInt("editorLevel", state.editorLevel)
            putInt("promoLevel", state.promoLevel)
            apply()
        }
    }

    fun load() {
        state = GameState(
            money              = prefs.getLong("money", 0L),
            subscribers        = prefs.getLong("subscribers", 0L),
            videoCount         = prefs.getLong("videoCount", 0L),
            moneyPerClick      = prefs.getLong("moneyPerClick", 10L),
            moneyPerSec        = prefs.getLong("moneyPerSec", 0L),
            subscribersPerVideo = prefs.getLong("subscribersPerVideo", 1L),
            cameraLevel        = prefs.getInt("cameraLevel", 1),
            micLevel           = prefs.getInt("micLevel", 1),
            lightLevel         = prefs.getInt("lightLevel", 1),
            editorLevel        = prefs.getInt("editorLevel", 1),
            promoLevel         = prefs.getInt("promoLevel", 0)
        )
    }

    fun formatNumber(n: Long): String = when {
        n >= 1_000_000_000 -> "%.1fB".format(n / 1_000_000_000.0)
        n >= 1_000_000     -> "%.1fM".format(n / 1_000_000.0)
        n >= 1_000         -> "%.1fK".format(n / 1_000.0)
        else               -> n.toString()
    }
}

data class Upgrade(
    val name: String,
    val description: String,
    val id: String,
    val baseCost: Long
)
