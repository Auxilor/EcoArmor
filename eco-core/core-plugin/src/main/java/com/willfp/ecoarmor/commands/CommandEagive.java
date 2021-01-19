package com.willfp.ecoarmor.commands;

import com.willfp.eco.util.command.AbstractCommand;
import com.willfp.eco.util.command.AbstractTabCompleter;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.ArmorSets;
import com.willfp.ecoarmor.sets.meta.ArmorSlot;
import com.willfp.ecoarmor.upgrades.crystal.UpgradeCrystal;
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
            sender.sendMessage(this.getPlugin().getLangYml().getMessage("needs-player"));
            return;
        }

        if (args.size() == 1) {
            sender.sendMessage(this.getPlugin().getLangYml().getMessage("needs-item"));
            return;
        }

        String recieverName = args.get(0);
        Player reciever = Bukkit.getPlayer(recieverName);

        if (reciever == null) {
            sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-player"));
            return;
        }

        String itemName = args.get(1);
        if (!itemName.contains(":")) {
            sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-item"));
            return;
        }

        if (itemName.split(":")[0].equals("set")) {
            ArmorSet set = ArmorSets.getByName(itemName.split(":")[1]);
            if (set == null) {
                sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-item"));
                return;
            }

            String message = this.getPlugin().getLangYml().getMessage("give-success");
            message = message.replace("%item%", set.getName() + " Set").replace("%recipient%", reciever.getName());
            sender.sendMessage(message);
            for (ArmorSlot slot : ArmorSlot.values()) {
                reciever.getInventory().addItem(set.getItemStack(slot));
            }

            return;
        }

        if (itemName.split(":")[0].equals("crystal")) {
            UpgradeCrystal crystal = UpgradeCrystal.getByName(itemName.split(":")[1]);
            if (crystal == null) {
                sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-item"));
                return;
            }

            String message = this.getPlugin().getLangYml().getMessage("give-success");
            message = message.replace("%item%", crystal.getItemStack().getItemMeta().getDisplayName()).replace("%recipient%", reciever.getName());
            sender.sendMessage(message);
            reciever.getInventory().addItem(crystal.getItemStack());

            return;
        }

        if (itemName.split(":")[0].equals("shard")) {
            ArmorSet set = ArmorSets.getByName(itemName.split(":")[1]);
            if (set == null) {
                sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-item"));
                return;
            }

            String message = this.getPlugin().getLangYml().getMessage("give-success");
            message = message.replace("%item%", set.getAdvancementShardItem().getItemMeta().getDisplayName()).replace("%recipient%", reciever.getName());
            sender.sendMessage(message);
            reciever.getInventory().addItem(set.getAdvancementShardItem());

            return;
        }

        sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-item"));
    }
}
