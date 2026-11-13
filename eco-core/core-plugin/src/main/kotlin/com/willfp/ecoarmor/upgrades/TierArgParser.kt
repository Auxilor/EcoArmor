package com.willfp.ecoarmor.upgrades

import com.willfp.ecoarmor.sets.ArmorUtils
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import wiki.ecohub.game.item.editItemMeta
import wiki.ecohub.game.item.lookup.LookupArgParser
import java.util.function.Predicate

class TierArgParser : LookupArgParser {
    override fun parseArguments(
        args: Array<out String>,
        item: ItemStack
    ): Predicate<ItemStack>? {
        var tier: Tier? = null

        for (arg in args) {
            val split = arg.split(":").toTypedArray()
            if (split.size == 1 || !split[0].equals("tier", ignoreCase = true)) {
                continue
            }
            val match = Tiers.getByID(split[1].lowercase()) ?: continue
            tier = match
            break
        }

        tier ?: return null

        item.editItemMeta<ItemMeta> { ArmorUtils.setTierKey(it, tier) }

        return Predicate { test ->
            val testMeta = test.itemMeta ?: return@Predicate false
            tier == ArmorUtils.getTier(testMeta)
        }
    }

    override fun serializeBack(item: ItemStack): String? {
        val tier = item.itemMeta?.let { ArmorUtils.getTier(it) } ?: return null

        return "tier:${tier.id}"
    }
}
