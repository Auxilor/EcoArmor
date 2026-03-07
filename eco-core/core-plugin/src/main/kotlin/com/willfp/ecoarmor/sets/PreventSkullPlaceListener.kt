package com.willfp.ecoarmor.sets

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

object PreventSkullPlaceListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlace(event: BlockPlaceEvent) {
        if (ArmorUtils.getSetOnItem(event.itemInHand) != null) {
            event.isCancelled = true
        }
    }
}