package com.willfp.ecoarmor.display

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.display.DisplayPriority
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.ecoarmor.sets.ArmorSlot
import com.willfp.ecoarmor.sets.ArmorUtils
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta

class ArmorDisplay(plugin: EcoPlugin) : DisplayModule(plugin, DisplayPriority.LOWEST) {
    override fun display(
        itemStack: ItemStack,
        player: Player?,
        vararg args: Any
    ) {
        val meta = itemStack.itemMeta ?: return

        val fis = FastItemStack.wrap(itemStack)

        val set = ArmorUtils.getSetOnItem(meta)

        if (set == null) {
            val crystalTier = ArmorUtils.getCrystalTier(meta)

            if (crystalTier != null) {
                val lore = fis.lore
                lore.addAll(FastItemStack.wrap(crystalTier.crystal).lore)
                fis.lore = lore
            }

            val shardSet = ArmorUtils.getShardSet(meta)

            if (shardSet != null) {
                val lore = fis.lore
                lore.addAll(FastItemStack.wrap(shardSet.advancementShardItem).lore)
                itemStack.itemMeta = shardSet.advancementShardItem.itemMeta
                FastItemStack.wrap(itemStack).lore = lore
            }

            return
        }

        val slot = ArmorSlot.getSlot(itemStack) ?: return

        val slotStack: ItemStack = if (ArmorUtils.isAdvanced(meta)) {
            set.getAdvancedItemStack(slot)
        } else {
            set.getItemStack(slot)
        }

        val slotMeta = slotStack.itemMeta ?: return

        val tier = ArmorUtils.getTier(meta) ?: return
        val lore = FastItemStack.wrap(slotStack).lore.map { it.replace("%tier%", tier.displayName) }.toMutableList()
        meta.addItemFlags(*slotMeta.itemFlags.toTypedArray())

        if (meta.hasLore()) {
            lore.addAll(fis.lore)
        }

        if (player != null) {
            val lines = mutableListOf<String>()

            lines.addAll(if (ArmorUtils.isAdvanced(meta)) {
                set.advancedHolder.conditions.getNotMetLines(player).map { Display.PREFIX + it }
            } else {
                set.regularHolder.conditions.getNotMetLines(player).map { Display.PREFIX + it }
            })

            lines.addAll(
                set.getSpecificHolder(itemStack)?.conditions?.getNotMetLines(player)?.map { Display.PREFIX + it }
                    ?: emptyList()
            )

            if (lines.isNotEmpty()) {
                lore.add(Display.PREFIX)
                lore.addAll(lines)
            }
        }

        if (this.plugin.configYml.getBool("update-item-names")) {
            @Suppress("DEPRECATION")
            meta.setDisplayName(slotMeta.displayName)
        }

        if (meta is LeatherArmorMeta && slotMeta is LeatherArmorMeta
            && this.plugin.configYml.getBool("update-leather-colors")
        ) {
            meta.setColor(slotMeta.color)
        }

        itemStack.itemMeta = meta
        FastItemStack.wrap(itemStack).lore = lore
    }
}
