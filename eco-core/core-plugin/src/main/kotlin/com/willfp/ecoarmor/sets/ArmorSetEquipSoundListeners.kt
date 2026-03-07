package com.willfp.ecoarmor.sets

import com.willfp.eco.core.sound.PlayableSound
import com.willfp.ecoarmor.api.event.PlayerArmorSetEquipEvent
import com.willfp.ecoarmor.api.event.PlayerArmorSetUnequipEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ArmorSetEquipSoundListeners: Listener {
    @EventHandler
    fun handleEquip(event: PlayerArmorSetEquipEvent) {
        val path = if (event.advanced) "sounds.advancedEquip" else "sounds.equip"
        val player = event.player
        PlayableSound.create(event.set.config.getSubsection(path))?.playTo(player)
    }

    @EventHandler
    fun handleUnequip(event: PlayerArmorSetUnequipEvent) {
        val player = event.player
        PlayableSound.create(event.set.config.getSubsection("sounds.unequip"))?.playTo(player)
    }
}