package com.willfp.ecoarmor.api.event

import com.willfp.ecoarmor.upgrades.Tier
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.ItemStack

class ArmorTierEvent(
    who: Player,
    val item: ItemStack,
    val tier: Tier,
    val previousTier: Tier?
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