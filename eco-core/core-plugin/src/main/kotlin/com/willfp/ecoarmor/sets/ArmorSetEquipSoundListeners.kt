package com.willfp.ecoarmor.sets

import com.willfp.ecoarmor.api.event.PlayerArmorSetEquipEvent
import com.willfp.ecoarmor.api.event.PlayerArmorSetUnequipEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ArmorSetEquipSoundListeners: Listener {
    @EventHandler
    fun handleEquip(event: PlayerArmorSetEquipEvent) {
        val sound = (if (event.advanced) event.set.advancedEquipSound else event.set.equipSound) ?: return
        sound.play(event.player)
    }

    @EventHandler
    fun handleUnequip(event: PlayerArmorSetUnequipEvent) {
        val sound = event.set.unequipSound ?: return
        sound.play(event.player)
    }
}