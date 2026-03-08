package com.willfp.ecoarmor.commands

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoarmor.plugin
import org.bukkit.command.CommandSender

object CommandEcoArmor : PluginCommand(
    plugin,
    "ecoarmor",
    "ecoarmor.command.ecoarmor",
    false
) {
    init {
        addSubcommand(CommandReload)
            .addSubcommand(CommandGive)
    }

    override fun onExecute(sender: CommandSender, args: List<String>) {
        sender.sendMessage(
            plugin.langYml.getMessage("invalid-command")
        )
    }
}
