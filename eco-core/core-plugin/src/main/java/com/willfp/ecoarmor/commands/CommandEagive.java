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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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

        String fullItemKey = args.get(1);

        if (!fullItemKey.contains(":")) {
            sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-item"));
            return;
        }
        String[] fullItemSplit = fullItemKey.split(":");
        if (fullItemSplit.length == 1) {
            sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-item"));
            return;
        }
        String itemNamespace = fullItemSplit[0];
        String itemKey = fullItemSplit[1];

        List<ItemStack> items = new ArrayList<>();
        int amount = 1;

        if (itemKey.equalsIgnoreCase("set")) {
            ArmorSet set = ArmorSets.getByName(itemKey);
            if (set == null) {
                sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-item"));
                return;
            }

            String message = this.getPlugin().getLangYml().getMessage("give-success");
            message = message.replace("%item%", set.getName() + " Set").replace("%recipient%", reciever.getName());
            sender.sendMessage(message);

            boolean advanced = false;
            if (args.size() >= 4) {
                advanced = Boolean.parseBoolean(args.get(3));
            }

            if (args.size() >= 5) {
                try {
                    amount = Integer.parseInt(args.get(4));
                } catch (NumberFormatException e) {
                    amount = 1;
                }
            }

            if (args.size() >= 3) {
                ArmorSlot slot = ArmorSlot.getSlot(args.get(2));

                if (slot == null && !args.get(2).equalsIgnoreCase("full")) {
                    sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-item"));
                    return;
                }

                if (advanced) {
                    if (slot != null) {
                        items.add(set.getAdvancedItemStack(slot));
                    } else {
                        for (ArmorSlot slot2 : ArmorSlot.values()) {
                            items.add(set.getAdvancedItemStack(slot2));
                        }
                    }
                } else {
                    if (slot != null) {
                        items.add(set.getItemStack(slot));
                    } else {
                        for (ArmorSlot slot2 : ArmorSlot.values()) {
                            items.add(set.getItemStack(slot2));
                        }
                    }
                }
            } else {
                for (ArmorSlot slot : ArmorSlot.values()) {
                    if (advanced) {
                        items.add(set.getAdvancedItemStack(slot));
                    } else {
                        items.add(set.getItemStack(slot));
                    }
                }

            }
        }

        if (itemNamespace.equalsIgnoreCase("crystal")) {
            UpgradeCrystal crystal = UpgradeCrystal.getByName(itemKey);
            if (crystal == null) {
                sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-item"));
                return;
            }

            String message = this.getPlugin().getLangYml().getMessage("give-success");
            message = message.replace("%item%", crystal.getItemStack().getItemMeta().getDisplayName()).replace("%recipient%", reciever.getName());
            sender.sendMessage(message);
            items.add(crystal.getItemStack());

            if (args.size() >= 3) {
                try {
                    amount = Integer.parseInt(args.get(2));
                } catch (NumberFormatException e) {
                    amount = 1;
                }
            }
        }

        if (itemNamespace.equalsIgnoreCase("shard")) {
            ArmorSet set = ArmorSets.getByName(itemKey);
            if (set == null) {
                sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-item"));
                return;
            }

            String message = this.getPlugin().getLangYml().getMessage("give-success");
            message = message.replace("%item%", set.getAdvancementShardItem().getItemMeta().getDisplayName()).replace("%recipient%", reciever.getName());
            sender.sendMessage(message);
            items.add(set.getAdvancementShardItem());

            if (args.size() >= 3) {
                try {
                    amount = Integer.parseInt(args.get(2));
                } catch (NumberFormatException e) {
                    amount = 1;
                }
            }
        }

        for (ItemStack item : items) {
            for (int i = 0; i < amount; i++) {
                reciever.getInventory().addItem(item);
            }
        }

        sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-item"));
    }
}
