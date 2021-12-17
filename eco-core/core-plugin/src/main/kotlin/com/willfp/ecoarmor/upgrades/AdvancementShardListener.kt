package com.willfp.ecoarmor.upgrades

import com.willfp.eco.core.EcoPlugin
import com.willfp.ecoarmor.sets.ArmorSets
import com.willfp.ecoarmor.sets.ArmorUtils
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class AdvancementShardListener(private val plugin: EcoPlugin) : Listener {
    @EventHandler
    fun onDrag(event: InventoryClickEvent) {
        if (event.whoClicked.gameMode == GameMode.CREATIVE) {
            return
        }
        val current = event.currentItem ?: return
        val cursor = event.cursor ?: return

        val cursorMeta = cursor.itemMeta ?: return

        val shardSet = cursorMeta.persistentDataContainer.get(
            plugin.namespacedKeyFactory.create("advancement-shard"),
            PersistentDataType.STRING
        ) ?: return

        val set = ArmorUtils.getSetOnItem(current) ?: return

        if (ArmorSets.getByID(shardSet)?.id != set.id) {
            return
        }

        if (current.type == Material.AIR) {
            return
        }

        if (ArmorUtils.isAdvanced(current)) {
            return
        }

        ArmorUtils.setAdvanced(current, true)

        if (cursor.amount > 1) {
            cursor.amount -= 1
            event.whoClicked.setItemOnCursor(cursor)
        } else {
            event.whoClicked.setItemOnCursor(ItemStack(Material.AIR))
        }

        event.isCancelled = true
    }
}