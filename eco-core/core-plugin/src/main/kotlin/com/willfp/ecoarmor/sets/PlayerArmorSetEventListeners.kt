package com.willfp.ecoarmor.sets

import com.willfp.eco.core.events.ArmorChangeEvent
import com.willfp.ecoarmor.api.event.PlayerArmorSetEquipEvent
import com.willfp.ecoarmor.api.event.PlayerArmorSetUnequipEvent
import com.willfp.ecoarmor.sets.ArmorUtils
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PlayerArmorSetEventListeners : Listener {
    // disgusting
    @EventHandler
    fun handle(event: ArmorChangeEvent) {
        val setBefore = ArmorUtils.getSetOn(event.before)
        val advancedBefore = ArmorUtils.isWearingAdvanced(event.before)
        val setAfter = ArmorUtils.getSetOn(event.after)
        val advancedAfter = ArmorUtils.isWearingAdvanced(event.after)

        if (setBefore == setAfter && advancedBefore == advancedAfter) {
            return
        }

        if (setBefore != null) {
            Bukkit.getPluginManager().callEvent(
                PlayerArmorSetUnequipEvent(event.player, setBefore, advancedBefore)
            )
        }

        if (setAfter != null) {
            Bukkit.getPluginManager().callEvent(
                PlayerArmorSetEquipEvent(event.player, setAfter, advancedAfter)
            )
        }
    }
}
