package com.willfp.ecoarmor.util

import com.willfp.eco.core.events.ArmorChangeEvent
import com.willfp.ecoarmor.sets.ArmorSet
import com.willfp.ecoarmor.sets.ArmorUtils
import com.willfp.libreforge.LibReforgePlugin
import com.willfp.libreforge.updateEffects
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityResurrectEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.ItemStack

class EffectListener(
    private val plugin: LibReforgePlugin
) : Listener {
    @EventHandler
    fun onArmorChange(event: ArmorChangeEvent) {
        val player = event.player

        for (itemStack in player.inventory.armorContents) {
            if (itemStack == null) {
                continue
            }

            val tier = ArmorUtils.getTier(itemStack) ?: continue
            ArmorUtils.setTier(itemStack, tier)
        }

        player.updateEffects()
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        plugin.scheduler.run { player.updateEffects() }
    }

    @EventHandler
    fun onTotem(event: EntityResurrectEvent) {
        val player = event.entity as? Player ?: return

        plugin.scheduler.run { player.updateEffects() }
    }

    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        plugin.scheduler.run { player.updateEffects() }
    }

    @EventHandler
    fun onWorldChange(event: PlayerChangedWorldEvent) {
        val player = event.player
        plugin.scheduler.run { player.updateEffects() }
    }

    @EventHandler
    fun handleSounds(event: ArmorChangeEvent) {
        val player = event.player

        val new = getSetOnItems(event.after) ?: return
        val newAdvanced = isAdvanced(event.after)
        val prev = getSetOnItems(event.before)
        val prevAdvanced = isAdvanced(event.before)
        if (new == prev && newAdvanced == prevAdvanced) {
            return
        }

        if (new == prev) {
            if (newAdvanced && !prevAdvanced) {
                new.advancedEquipSound?.play(player)
            }
        } else {
            prev?.unequipSound?.play(player)
            if (newAdvanced) {
                new.advancedEquipSound?.play(player)
            } else {
                new.equipSound?.play(player)
            }
        }
    }

    private fun getSetOnItems(items: List<ItemStack?>): ArmorSet? {
        var set: ArmorSet? = null

        for (item in items) {
            if (item == null || item.type == Material.AIR) {
                return null
            }

            val found = ArmorUtils.getSetOnItem(item) ?: return null

            if (set != null && set != found) {
                return null
            }

            set = found
        }

        return set
    }

    private fun isAdvanced(items: List<ItemStack?>): Boolean {
        for (item in items) {
            if (item == null) {
                return false
            }

            if (!ArmorUtils.isAdvanced(item)) {
                return false
            }
        }

        return true
    }
}
