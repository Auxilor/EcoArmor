package com.willfp.ecoarmor.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.CommandHandler
import com.willfp.eco.core.command.TabCompleteHandler
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecoarmor.sets.ArmorSets
import com.willfp.ecoarmor.sets.ArmorSlot
import com.willfp.ecoarmor.sets.ArmorSlot.Companion.getSlot
import com.willfp.ecoarmor.sets.util.ArmorUtils
import com.willfp.ecoarmor.upgrades.Tier
import com.willfp.ecoarmor.upgrades.Tiers
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.StringUtil
import java.util.stream.Collectors

class CommandGive(plugin: EcoPlugin) : Subcommand(plugin, "give", "ecoarmor.command.give", false) {
    private val items: Collection<String>
        get() = ArmorSets.values().map { "set:${it.id}" } union ArmorSets.values()
            .map { "shard:${it.id}" } union Tiers.values().map { "crystal:${it.id}" }

    private val slots: Collection<String>
        get() = ArmorSlot.values().map { it.name.lowercase() }.toMutableList().apply { add("full") }

    private val tiers: Collection<String>
        get() = Tiers.values().map { it.id }

    private val numbers = listOf(
        "1",
        "2",
        "3",
        "4",
        "5",
        "10",
        "32",
        "64"
    )

    override fun getHandler(): CommandHandler {
        return CommandHandler { sender: CommandSender, args: List<String> ->
            if (args.isEmpty()) {
                sender.sendMessage(plugin.langYml.getMessage("needs-player"))
                return@CommandHandler
            }

            if (args.size == 1) {
                sender.sendMessage(plugin.langYml.getMessage("needs-item"))
                return@CommandHandler
            }

            val recieverName = args[0]
            val reciever = Bukkit.getPlayer(recieverName)

            if (reciever == null) {
                sender.sendMessage(plugin.langYml.getMessage("invalid-player"))
                return@CommandHandler
            }

            val fullItemKey = args[1]

            if (!fullItemKey.contains(":")) {
                sender.sendMessage(plugin.langYml.getMessage("invalid-item"))
                return@CommandHandler
            }

            val fullItemSplit = fullItemKey.split(":").toTypedArray()

            if (fullItemSplit.size == 1) {
                sender.sendMessage(plugin.langYml.getMessage("invalid-item"))
                return@CommandHandler
            }

            val itemNamespace = fullItemSplit[0]
            val itemKey = fullItemSplit[1]
            val toGive = mutableListOf<ItemStack>()
            var amount = 1

            if (itemNamespace.equals("set", ignoreCase = true)) {
                val set = ArmorSets.getByID(itemKey)

                if (set == null) {
                    sender.sendMessage(plugin.langYml.getMessage("invalid-item"))
                    return@CommandHandler
                }

                var message = plugin.langYml.getMessage("give-success")

                message = message.replace("%item%", set.id + " Set").replace("%recipient%", reciever.name)

                sender.sendMessage(message)

                var advanced = false
                var tier: Tier? = null
                val slots = mutableListOf<ArmorSlot>()
                if (args.size >= 3) {
                    val slot = getSlot(args[2])
                    if (slot == null) {
                        if (!args[2].equals("full", ignoreCase = true)) {
                            sender.sendMessage(plugin.langYml.getMessage("invalid-item"))
                            return@CommandHandler
                        }
                    }
                    if (slot == null) {
                        slots.addAll(ArmorSlot.values())
                    } else {
                        slots.add(slot)
                    }
                } else {
                    slots.addAll(ArmorSlot.values())
                }
                if (args.size >= 4) {
                    advanced = args[3].toBoolean()
                }
                if (args.size >= 5) {
                    tier = Tiers.getByID(args[4])
                }
                if (args.size >= 6) {
                    amount = args[5].toIntOrNull() ?: amount
                }
                for (slot in slots) {
                    toGive.add(if (advanced) set.getAdvancedItemStack(slot) else set.getItemStack(slot))
                }
                for (item in ArrayList(toGive)) {
                    val currTear = tier ?: set.getDefaultTier(getSlot(item))
                    toGive.remove(item)
                    ArmorUtils.setTier(item, currTear)
                    toGive.add(item)
                }
            }
            if (itemNamespace.equals("crystal", ignoreCase = true)) {
                val tier = Tiers.getByID(itemKey)
                if (tier == null) {
                    sender.sendMessage(plugin.langYml.getMessage("invalid-item"))
                    return@CommandHandler
                }
                var message = plugin.langYml.getMessage("give-success")
                message =
                    message.replace("%item%", tier.crystal.itemMeta!!.displayName).replace("%recipient%", reciever.name)
                sender.sendMessage(message)
                toGive.add(tier.crystal)
                if (args.size >= 3) {
                    amount = args[2].toIntOrNull() ?: amount
                }
            }
            if (itemNamespace.equals("shard", ignoreCase = true)) {
                val set = ArmorSets.getByID(itemKey)
                if (set == null) {
                    sender.sendMessage(plugin.langYml.getMessage("invalid-item"))
                    return@CommandHandler
                }
                var message = plugin.langYml.getMessage("give-success")
                message = message.replace("%item%", set.advancementShardItem.itemMeta!!.displayName)
                    .replace("%recipient%", reciever.name)
                sender.sendMessage(message)
                toGive.add(set.advancementShardItem)
                if (args.size >= 3) {
                    amount = args[2].toIntOrNull() ?: amount
                }
            }
            if (toGive.isEmpty()) {
                sender.sendMessage(plugin.langYml.getMessage("invalid-item"))
                return@CommandHandler
            }
            for (item in toGive) {
                item.amount = amount
                reciever.inventory.addItem(item)
            }
        }
    }

    override fun getTabCompleter(): TabCompleteHandler {
        return TabCompleteHandler { _, args ->
            val completions = mutableListOf<String>()
            if (args.isEmpty()) {
                // Currently, this case is not ever reached
                return@TabCompleteHandler items.toList()
            }
            if (args.size == 1) {
                StringUtil.copyPartialMatches(
                    args[0],
                    Bukkit.getOnlinePlayers().stream().map { obj: Player -> obj.name }
                        .collect(Collectors.toList()),
                    completions)
                return@TabCompleteHandler completions
            }
            if (args.size == 2) {
                StringUtil.copyPartialMatches(args[1], items, completions)
                completions.sort()
                return@TabCompleteHandler completions
            }
            if (args[1].startsWith("set:")) {
                if (args.size == 3) {
                    StringUtil.copyPartialMatches(args[2], slots, completions)
                    completions.sort()
                    return@TabCompleteHandler completions
                }
                if (args.size == 4) {
                    StringUtil.copyPartialMatches(args[3], listOf("true", "false"), completions)
                    completions.sort()
                    return@TabCompleteHandler completions
                }
                if (args.size == 5) {
                    StringUtil.copyPartialMatches(args[4], tiers, completions)
                    completions.sort()
                    return@TabCompleteHandler completions
                }
                if (args.size == 6) {
                    StringUtil.copyPartialMatches(args[5], numbers, completions)
                    return@TabCompleteHandler completions
                }
            } else {
                if (args.size == 3) {
                    StringUtil.copyPartialMatches(args[2], numbers, completions)
                    return@TabCompleteHandler completions
                }
            }
            ArrayList(0)
        }
    }
}