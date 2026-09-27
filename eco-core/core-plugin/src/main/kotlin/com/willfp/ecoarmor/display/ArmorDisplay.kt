package com.willfp.ecoarmor.display

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.display.DisplayContext
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.display.DisplayPriority
import com.willfp.eco.core.fast.fast
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.formatEcoRich
import com.willfp.eco.util.toLegacy
import com.willfp.ecoarmor.plugin
import com.willfp.ecoarmor.sets.ArmorSlot
import com.willfp.ecoarmor.sets.ArmorUtils
import com.willfp.libreforge.SimpleProvidedHolder
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta

object ArmorDisplay : DisplayModule(plugin, DisplayPriority.LOWEST) {
    override fun display(context: DisplayContext) {
        val itemStack = context.itemStack
        val meta = itemStack.itemMeta ?: return
        val set = ArmorUtils.getSetOnItem(meta)

        if (set == null) {
            val crystalTier = ArmorUtils.getCrystalTier(meta)
            if (crystalTier != null) {
                context.lore.append(crystalTier.crystal.storedLore())
            }

            val shardSet = ArmorUtils.getShardSet(meta)
            if (shardSet != null) {
                context.lore.append(shardSet.advancementShardItem.storedLore())
                itemStack.itemMeta = shardSet.advancementShardItem.itemMeta
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
        val appliedTiers = ArmorUtils.getAppliedTiers(meta)

        val tierPlaceholder = if (appliedTiers.size > 1) {
            val separator = plugin.configYml.getString("armor-display.tier-list-separator")

            when (plugin.configYml.getString("armor-display.tier-stack-format").lowercase()) {
                "multiple" -> {
                    val counts = appliedTiers.groupingBy { it }.eachCount()
                    appliedTiers.distinct().joinToString(separator) {
                        val count = counts.getValue(it)
                        if (count > 1) "${count}x ${it.displayName}" else it.displayName
                    }
                }

                "numeral" -> {
                    val counts = appliedTiers.groupingBy { it }.eachCount()
                    appliedTiers.distinct().joinToString(separator) {
                        val count = counts.getValue(it)
                        if (count > 1) "${it.displayName} ${NumberUtils.toNumeral(count)}" else it.displayName
                    }
                }

                else -> appliedTiers.joinToString(separator) { it.displayName }
            }
        } else {
            tier.displayName
        }

        context.lore.prepend(
            slotStack.storedLore()
                .map { it.toLegacy().replace("%tier%", tierPlaceholder) }
                .formatEcoRich(context.placeholderContext)
        )

        meta.addItemFlags(*slotMeta.itemFlags.toTypedArray())

        val player = context.player

        if (player != null) {
            val lines = mutableListOf<Component>()

            lines.addAll(if (ArmorUtils.isAdvanced(meta)) {
                SimpleProvidedHolder(set.advancedHolder).getNotMetLineComponents(player)
            } else {
                SimpleProvidedHolder(set.regularHolder).getNotMetLineComponents(player)
            })

            // Lovely.
            lines.addAll(set.getSpecificHolder(itemStack)?.getNotMetLineComponents(player) ?: emptyList())

            if (lines.isNotEmpty()) {
                context.lore.append(listOf(Component.empty()) + lines)
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
    }

    private fun ItemStack.storedLore(): List<Component> =
        this.fast().loreComponents.map { Display.stripDisplayMarker(it) }
}
