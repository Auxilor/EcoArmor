package com.willfp.ecoarmor.commands

import com.willfp.ecoarmor.sets.ArmorSet
import com.willfp.ecoarmor.sets.ArmorSets
import com.willfp.ecoarmor.sets.ArmorSlot
import com.willfp.ecoarmor.sets.ArmorSlot.Companion.getSlot
import com.willfp.ecoarmor.sets.ArmorUtils
import com.willfp.ecoarmor.upgrades.Tiers
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import wiki.ecohub.core.command.CommandBuilder
import wiki.ecohub.core.command.CommandContext
import wiki.ecohub.game.entity.runOwned

private val amountCompletions = listOf("1", "2", "3", "4", "5", "10", "32", "64")

private val booleanCompletions = listOf("true", "false")

private const val SLOT_INDEX = 2
private const val ADVANCED_INDEX = 3
private const val TIER_INDEX = 4
private const val SET_AMOUNT_INDEX = 5

private class Gift(val name: String, val items: List<ItemStack>, val amount: Int)

/**
 * `give <player> set:<id> [slot|full] [advanced] [tier] [amount]`, or `crystal:<tier>` /
 * `shard:<set>` with an optional amount. Gives on the receiver's region, as the console runs off
 * it on Folia.
 */
internal fun CommandBuilder<CommandSender>.giveCommand() = subcommand(
    name = "give",
    permission = "ecoarmor.command.give"
) {
    executes {
        val receiverName = argument(0, "needs-player")
        val itemArg = argument(1, "needs-item")
        val receiver = Bukkit.getPlayer(receiverName) ?: fail("invalid-player")

        val split = itemArg.split(":").takeIf { it.size > 1 } ?: fail("invalid-item")
        val gift = when (split[0].lowercase()) {
            "set" -> setGift(split[1])
            "crystal" -> crystalGift(split[1])
            "shard" -> shardGift(split[1])
            else -> fail("invalid-item")
        }

        reply("give-success", "item" to gift.name, "recipient" to receiver.name)

        receiver.runOwned {
            for (item in gift.items) {
                item.amount = gift.amount
                receiver.inventory.addItem(item)
            }
        }
    }

    completes {
        when {
            args.size == 1 -> playerNames()
            args.size == 2 -> itemCompletions()
            args[1].startsWith("set:") -> setCompletions(args.size, args[1].removePrefix("set:"))
            args.size == SLOT_INDEX + 1 -> amountCompletions
            else -> emptyList()
        }
    }
}

private fun itemCompletions(): List<String> =
    ArmorSets.values().map { "set:${it.id}" } +
        ArmorSets.values().map { "shard:${it.id}" } +
        Tiers.values().map { "crystal:${it.id}" }

private fun setCompletions(size: Int, setId: String): List<String> = when (size) {
    SLOT_INDEX + 1 -> {
        val set = ArmorSets.getByID(setId)
        ArmorSlot.entries
            .filter { it != ArmorSlot.ELYTRA || set == null || set.elytraEnabled }
            .map { it.name.lowercase() } + "full"
    }

    ADVANCED_INDEX + 1 -> {
        booleanCompletions
    }

    TIER_INDEX + 1 -> {
        Tiers.values().map { it.id }
    }

    SET_AMOUNT_INDEX + 1 -> {
        amountCompletions
    }

    else -> {
        emptyList()
    }
}

private fun ArmorSet.slotsFor(arg: String?, fail: () -> Nothing): List<ArmorSlot> {
    val all = ArmorSlot.entries.filter { it != ArmorSlot.ELYTRA || elytraEnabled }
    val slot = arg?.let { getSlot(it) }

    return when {
        arg == null || slot == null && arg.equals("full", ignoreCase = true) -> all
        slot == null || slot == ArmorSlot.ELYTRA && !elytraEnabled -> fail()
        else -> listOf(slot)
    }
}

private fun CommandContext<CommandSender>.setGift(id: String): Gift {
    val set = ArmorSets.getByID(id) ?: fail("invalid-item")
    val slots = set.slotsFor(args.getOrNull(SLOT_INDEX)) { fail("invalid-item") }
    val advanced = args.getOrNull(ADVANCED_INDEX).toBoolean()
    val tier = args.getOrNull(TIER_INDEX)?.let { Tiers.getByID(it) }

    val items = slots.map { slot ->
        val item = (if (advanced) set.getAdvancedItemStack(slot) else set.getItemStack(slot)).clone()
        ArmorUtils.setTier(item, tier ?: set.getDefaultTier(getSlot(item)))
        item
    }

    return Gift("${set.id} Set", items, args.getOrNull(SET_AMOUNT_INDEX)?.toIntOrNull() ?: 1)
}

@Suppress("DEPRECATION")
private fun CommandContext<CommandSender>.crystalGift(id: String): Gift {
    val tier = Tiers.getByID(id) ?: fail("invalid-item")
    return Gift(tier.crystal.itemMeta!!.displayName, listOf(tier.crystal.clone()), amountAt(SLOT_INDEX))
}

@Suppress("DEPRECATION")
private fun CommandContext<CommandSender>.shardGift(id: String): Gift {
    val set = ArmorSets.getByID(id) ?: fail("invalid-item")
    val shard = set.advancementShardItem
    return Gift(shard.itemMeta!!.displayName, listOf(shard.clone()), amountAt(SLOT_INDEX))
}

private fun CommandContext<CommandSender>.amountAt(index: Int): Int = args.getOrNull(index)?.toIntOrNull() ?: 1
