package com.willfp.ecoarmor.sets

import com.willfp.eco.util.SoundConfigUtils
import com.willfp.ecoarmor.api.event.PlayerArmorSetEquipEvent
import com.willfp.ecoarmor.api.event.PlayerArmorSetUnequipEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ArmorSetEquipSoundListeners: Listener {
    @EventHandler
    fun handleEquip(event: PlayerArmorSetEquipEvent) {
        val path = if (event.advanced) "sounds.advancedEquip" else "sounds.equip"
        SoundConfigUtils.playIfEnabled(event.set.config, event.player, path)
    }

    @EventHandler
    fun handleUnequip(event: PlayerArmorSetUnequipEvent) {
        val path = "sounds.unequip"
        SoundConfigUtils.playIfEnabled(event.set.config, event.player, path)
    }
}