package com.willfp.ecoarmor.util

import org.bukkit.Sound
import org.bukkit.entity.Player

class PlayableSound(
    private val sound: Sound,
    private val pitch: Double,
    val volume: Double
) {
    fun play(player: Player) {
        player.playSound(player.location, sound, volume.toFloat(), pitch.toFloat())
    }
}