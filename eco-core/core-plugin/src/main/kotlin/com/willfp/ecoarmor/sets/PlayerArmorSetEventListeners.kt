package com.willfp.ecoarmor.sets

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object PlayerArmorSetEventListeners : Listener {
    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        ArmorUtils.removeFromCache(event.player)
    }
}
