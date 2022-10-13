package com.willfp.ecoarmor.commands

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoarmor.sets.ArmorSets
import com.willfp.libreforge.LibReforgePlugin
import com.willfp.libreforge.lrcdb.CommandExport
import com.willfp.libreforge.lrcdb.CommandImport
import com.willfp.libreforge.lrcdb.ExportableConfig
import org.bukkit.command.CommandSender

class CommandEcoarmor(plugin: LibReforgePlugin) : PluginCommand(plugin, "ecoarmor", "ecoarmor.command.ecoarmor", false) {
    init {
        addSubcommand(CommandReload(plugin))
            .addSubcommand(CommandGive(plugin))
            .addSubcommand(CommandImport("sets", plugin))
            .addSubcommand(CommandExport(plugin) {
                ArmorSets.values().map {
                    ExportableConfig(
                        it.id,
                        it.config
                    )
                }
            })
    }

    override fun onExecute(sender: CommandSender, args: List<String>) {
        sender.sendMessage(
            plugin.langYml.getMessage("invalid-command")
        )
    }
}
