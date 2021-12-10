package com.willfp.ecoarmor.sets

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.NumberUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.persistence.PersistentDataType

class EffectiveDurabilityListener(private val plugin: EcoPlugin) : Listener {
    @EventHandler
    fun listener(event: PlayerItemDamageEvent) {
        val itemStack = event.item
        val meta = itemStack.itemMeta ?: return
        val container = meta.persistentDataContainer
        val effectiveDurability =
            container.get(plugin.namespacedKeyFactory.create("effective-durability"), PersistentDataType.INTEGER)
                ?: return
        val maxDurability = itemStack.type.maxDurability.toInt()
        val ratio = effectiveDurability.toDouble() / maxDurability
        val chance = 1 / ratio
        if (NumberUtils.randFloat(0.0, 1.0) > chance) {
            event.isCancelled = true
        }
    }
}