package com.willfp.ecoarmor.upgrades

import com.willfp.ecoarmor.sets.ArmorUtils
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class CrystalListener : Listener {
    @EventHandler
    fun onDrag(event: InventoryClickEvent) {
        if (event.whoClicked.gameMode == GameMode.CREATIVE) {
            return
        }
        val current = event.currentItem ?: return
        val cursor = event.cursor ?: return

        val crystalTier = ArmorUtils.getCrystalTier(cursor) ?: return

        if (ArmorUtils.getSetOnItem(current) == null) {
            return
        }

        if (current.type == Material.AIR) {
            return
        }
        val previousTier = ArmorUtils.getTier(current)
        var allowed = false
        val requiredTiers = crystalTier.getRequiredTiersForApplication()
        if (requiredTiers.isEmpty() || requiredTiers.contains(previousTier)) {
            allowed = true
        }
        if (!allowed) {
            return
        }
        ArmorUtils.setTier(current, crystalTier)
        if (cursor.amount > 1) {
            cursor.amount = cursor.amount - 1
            event.whoClicked.setItemOnCursor(cursor)
        } else {
            event.whoClicked.setItemOnCursor(ItemStack(Material.AIR))
        }
        event.isCancelled = true
    }

    @EventHandler
    fun onPlaceCrystal(event: BlockPlaceEvent) {
        val item = event.itemInHand
        if (ArmorUtils.getCrystalTier(item) != null) {
            event.isCancelled = true
        }
    }
}