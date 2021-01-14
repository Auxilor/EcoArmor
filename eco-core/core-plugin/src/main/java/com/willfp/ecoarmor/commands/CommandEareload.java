package com.willfp.ecoarmor.commands;

import com.willfp.eco.util.command.AbstractCommand;
import com.willfp.eco.util.config.Configs;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandEareload extends AbstractCommand {
    /**
     * Instantiate a new /eareload command handler.
     *
     * @param plugin The plugin for the commands to listen for.
     */
    public CommandEareload(@NotNull final AbstractEcoPlugin plugin) {
        super(plugin, "eareload", "ecoarmor.reload", false);
    }

    @Override
    public void onExecute(@NotNull final CommandSender sender,
                          @NotNull final List<String> args) {
        this.getPlugin().reload();
        sender.sendMessage(Configs.LANG.getMessage("reloaded"));
    }
}
