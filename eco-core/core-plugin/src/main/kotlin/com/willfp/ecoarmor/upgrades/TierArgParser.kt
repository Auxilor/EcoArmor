package com.willfp.ecoarmor.upgrades

import com.willfp.eco.core.items.args.LookupArgParser
import com.willfp.ecoarmor.sets.ArmorUtils
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.function.Predicate

class TierArgParser : LookupArgParser {
    override fun parseArguments(
        args: Array<String>,
        meta: ItemMeta
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

        ArmorUtils.setTierKey(meta, tier)

        return Predicate { test ->
            val testMeta = test.itemMeta ?: return@Predicate false
            tier == ArmorUtils.getTier(testMeta)
        }
    }

    override fun serializeBack(meta: ItemMeta): String? {
        val tier = ArmorUtils.getTier(meta) ?: return null

        return "tier:${tier.id}"
    }
}
