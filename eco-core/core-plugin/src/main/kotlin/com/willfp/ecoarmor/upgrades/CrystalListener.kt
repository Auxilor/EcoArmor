package com.willfp.ecoarmor.upgrades

import com.willfp.eco.core.dragdrop.DragAndDropHandler
import com.willfp.eco.core.dragdrop.DragAndDropResult
import com.willfp.ecoarmor.api.event.ArmorTierEvent
import com.willfp.ecoarmor.sets.ArmorUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityPlaceEvent
import org.bukkit.inventory.ItemStack

object CrystalListener : Listener, DragAndDropHandler {
    override val id = "ecoarmor:crystal"

    override fun matches(cursor: ItemStack, current: ItemStack): Boolean {
        if (ArmorUtils.getCrystalTier(cursor) == null) return false
        if (ArmorUtils.getSetOnItem(current) == null) return false
        return true
    }

    override fun apply(player: Player, cursor: ItemStack, current: ItemStack): DragAndDropResult {
        val crystalTier = ArmorUtils.getCrystalTier(cursor) ?: return DragAndDropResult.DENIED
        val previousTier = ArmorUtils.getTier(current)

        val requiredTiers = crystalTier.getRequiredTiersForApplication()
        val allowed = requiredTiers.isEmpty() || requiredTiers.contains(previousTier)
        if (!allowed) return DragAndDropResult.DENIED

        val tierEvent = ArmorTierEvent(player, current, crystalTier, previousTier)
        Bukkit.getPluginManager().callEvent(tierEvent)
        if (tierEvent.isCancelled) return DragAndDropResult.DENIED

        ArmorUtils.setTier(current, crystalTier)
        return DragAndDropResult.APPLIED
    }

    @EventHandler
    fun onPlaceCrystal(event: BlockPlaceEvent) {
        val item = event.itemInHand
        if (ArmorUtils.getCrystalTier(item) != null) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlaceCrystalEntity(event: EntityPlaceEvent) {
        val player = event.player ?: return
        val item = player.inventory.getItem(event.hand) ?: return
        if (ArmorUtils.getCrystalTier(item) != null) {
            event.isCancelled = true
        }
    }
}
