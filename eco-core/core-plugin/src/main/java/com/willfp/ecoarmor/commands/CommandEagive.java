package com.willfp.ecoarmor.commands;

import com.willfp.eco.util.command.AbstractCommand;
import com.willfp.eco.util.command.AbstractTabCompleter;
import com.willfp.eco.util.config.Configs;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.ArmorSets;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandEagive extends AbstractCommand {
    /**
     * Instantiate a new /eagive command handler.
     *
     * @param plugin The plugin for the commands to listen for.
     */
    public CommandEagive(@NotNull final AbstractEcoPlugin plugin) {
        super(plugin, "eagive", "ecoarmor.give", false);
    }

    @Override
    public @Nullable AbstractTabCompleter getTab() {
        return new TabcompleterEagive();
    }

    @Override
    public void onExecute(@NotNull final CommandSender sender,
                          @NotNull final List<String> args) {
        if (args.isEmpty()) {
            sender.sendMessage(Configs.LANG.getMessage("needs-player"));
            return;
        }

        if (args.size() == 1) {
            sender.sendMessage(Configs.LANG.getMessage("needs-set"));
            return;
        }

        String recieverName = args.get(0);
        Player reciever = Bukkit.getPlayer(recieverName);

        if (reciever == null) {
            sender.sendMessage(Configs.LANG.getMessage("invalid-player"));
            return;
        }

        String setName = args.get(1);
        ArmorSet set = ArmorSets.getByName(setName);
        if (set == null) {
            sender.sendMessage(Configs.LANG.getMessage("invalid-set"));
            return;
        }

        String message = Configs.LANG.getMessage("give-success");
        message = message.replace("%set%", set.getName()).replace("%recipient%", reciever.getName());
        sender.sendMessage(message);
        reciever.getInventory().addItem(set.getHelmet());
        reciever.getInventory().addItem(set.getChestplate());
        reciever.getInventory().addItem(set.getLeggings());
        reciever.getInventory().addItem(set.getBoots());
    }
}
