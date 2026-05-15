package com.willfp.ecoarmor.api.event

import com.willfp.ecoarmor.sets.ArmorSet
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.ItemStack

class ArmorAdvanceEvent(
    who: Player,
    val item: ItemStack,
    val set: ArmorSet
) : PlayerEvent(who), Cancellable {
    private var cancelled = false

    override fun isCancelled() = cancelled

    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}