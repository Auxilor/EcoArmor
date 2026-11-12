package com.willfp.ecoarmor.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecoarmor.plugin
import com.willfp.ecoarmor.sets.ArmorSets
import com.willfp.ecoarmor.upgrades.Tiers
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import wiki.ecohub.core.plugin.startup.Prerequisite
import wiki.ecohub.core.stdlib.toNiceString
import wiki.ecohub.ui.text.StringUtils

object CommandReload : Subcommand(
    plugin,
    "reload",
    "ecoarmor.command.reload",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        // Reloading rebuilds shared registries, so it belongs on the global region.
        if (Prerequisite.HAS_FOLIA.isMet && !Bukkit.isGlobalTickThread()) {
            plugin.scheduler.global().run { onExecute(sender, args) }
            return
        }

        sender.sendMessage(
            plugin.langYml.getMessage("reloaded", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                .replace("%time%", plugin.reloadWithTime().toNiceString())
                .replace("%sets%", ArmorSets.values().size.toString())
                .replace("%tiers%", Tiers.values().size.toString())
        )
    }
}