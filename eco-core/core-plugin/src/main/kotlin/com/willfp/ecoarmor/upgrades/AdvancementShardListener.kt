package com.willfp.ecoarmor.upgrades

import com.willfp.eco.core.dragdrop.DragAndDropHandler
import com.willfp.eco.core.dragdrop.DragAndDropResult
import com.willfp.ecoarmor.api.event.ArmorAdvanceEvent
import com.willfp.ecoarmor.plugin
import com.willfp.ecoarmor.sets.ArmorSets
import com.willfp.ecoarmor.sets.ArmorUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object AdvancementShardListener : DragAndDropHandler {
    override val id = "ecoarmor:shard"

    override fun matches(cursor: ItemStack, current: ItemStack): Boolean {
        val shardSet = cursor.itemMeta?.persistentDataContainer?.get(
            plugin.namespacedKeyFactory.create("advancement-shard"),
            PersistentDataType.STRING
        ) ?: return false

        val set = ArmorUtils.getSetOnItem(current) ?: return false

        return ArmorSets.getByID(shardSet)?.id == set.id && !ArmorUtils.isAdvanced(current)
    }

    override fun apply(player: Player, cursor: ItemStack, current: ItemStack): DragAndDropResult {
        val set = ArmorUtils.getSetOnItem(current) ?: return DragAndDropResult.DENIED

        val advanceEvent = ArmorAdvanceEvent(player, current, set)
        Bukkit.getPluginManager().callEvent(advanceEvent)
        if (advanceEvent.isCancelled) return DragAndDropResult.DENIED

        ArmorUtils.setAdvanced(current, true)
        return DragAndDropResult.APPLIED
    }
}
