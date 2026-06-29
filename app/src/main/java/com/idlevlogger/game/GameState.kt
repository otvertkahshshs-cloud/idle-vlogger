package com.idlevlogger.game

data class GameState(
    var money: Long = 0L,
    var subscribers: Long = 0L,
    var videoCount: Long = 0L,
    var moneyPerClick: Long = 10L,
    var moneyPerSec: Long = 0L,
    var subscribersPerVideo: Long = 1L,
    var cameraLevel: Int = 1,
    var micLevel: Int = 1,
    var lightLevel: Int = 1,
    var editorLevel: Int = 1,
    var promoLevel: Int = 0
)
