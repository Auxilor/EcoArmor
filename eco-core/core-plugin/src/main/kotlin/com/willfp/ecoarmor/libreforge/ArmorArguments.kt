package com.willfp.ecoarmor.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoarmor.sets.ArmorSlot
import com.willfp.ecoarmor.sets.ArmorUtils
import com.willfp.ecoarmor.upgrades.Tiers
import org.bukkit.inventory.ItemStack

/**
 * The slots accepted by the slot argument, for documentation.
 */
internal val slotChoices = ArmorSlot.entries.map { it.name.lowercase() }

/**
 * Read the optional slot argument, returning null if it wasn't specified.
 *
 * Throws away invalid slots by returning [ArmorSlot] null, so callers should check
 * [Config.has] themselves to tell an absent slot from an invalid one.
 */
internal fun Config.getArmorSlot(key: String = "slot"): ArmorSlot? =
    ArmorSlot.getSlot(this.getString(key))

/**
 * Get if an item is an armor piece on one of the given [tiers].
 */
internal fun ItemStack.isOnTier(tiers: Collection<String>): Boolean {
    val meta = this.itemMeta ?: return false

    if (ArmorUtils.getSetOnItem(meta) == null) {
        return false
    }

    // Read from the meta rather than the item, as reading from the item writes
    // the default tier back to untiered pieces, which is too expensive to do
    // every time a condition is checked.
    val tier = ArmorUtils.getTier(meta) ?: Tiers.defaultTier

    return tiers.any { it.equals(tier.id, ignoreCase = true) }
}
