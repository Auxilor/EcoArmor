package com.willfp.ecoarmor.util

import com.willfp.eco.core.events.ArmorChangeEvent
import com.willfp.libreforge.updateEffects
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class EffectListener: Listener {
    @EventHandler
    fun armorEquipListener(event: ArmorChangeEvent) {
        val player = event.player

        player.updateEffects()
    }
}