package com.willfp.ecoarmor.commands;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.CommandHandler;
import com.willfp.eco.core.command.TabCompleteHandler;
import com.willfp.eco.core.command.impl.Subcommand;
import com.willfp.eco.core.config.updating.ConfigUpdater;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.ArmorSets;
import com.willfp.ecoarmor.sets.meta.ArmorSlot;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import com.willfp.ecoarmor.upgrades.Tier;
import com.willfp.ecoarmor.upgrades.Tiers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandGive extends Subcommand {
    /**
     * The cached names.
     */
    private static final List<String> ITEM_NAMES = new ArrayList<>();

    /**
     * The cached slots.
     */
    private static final List<String> SLOTS = new ArrayList<>();

    /**
     * The cached tiers.
     */
    private static final List<String> TIERS = new ArrayList<>();

    /**
     * The cached numbers.
     */
    private static final List<String> NUMBERS = Arrays.asList(
            "1",
            "2",
            "3",
            "4",
            "5",
            "10",
            "32",
            "64"
    );

    /**
     * Instantiate a new /eagive command handler.
     *
     * @param plugin The plugin for the commands to listen for.
     */
    public CommandGive(@NotNull final EcoPlugin plugin) {
        super(plugin, "give", "ecoarmor.command.give", false);
        reload();
    }

    /**
     * Called on reload.
     */
    @ConfigUpdater
    public static void reload() {
        ITEM_NAMES.clear();
        ITEM_NAMES.addAll(ArmorSets.values().stream().map(armorSet -> "set:" + armorSet.getId()).collect(Collectors.toList()));
        ITEM_NAMES.addAll(ArmorSets.values().stream().map(armorSet -> "shard:" + armorSet.getId()).collect(Collectors.toList()));
        ITEM_NAMES.addAll(Tiers.values().stream().map(crystal -> "crystal:" + crystal.getId()).collect(Collectors.toList()));
        SLOTS.addAll(Arrays.stream(ArmorSlot.values()).map(slot -> slot.name().toLowerCase()).collect(Collectors.toList()));
        SLOTS.add("full");
        TIERS.addAll(Tiers.values().stream().map(Tier::getId).collect(Collectors.toList()));
    }

    @Override
    public CommandHandler getHandler() {
        return (sender, args) -> {

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

            if (itemNamespace.equalsIgnoreCase("set")) {
                ArmorSet set = ArmorSets.getByID(itemKey);
                if (set == null) {
                    sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-item"));
                    return;
                }

                String message = this.getPlugin().getLangYml().getMessage("give-success");
                message = message.replace("%item%", set.getId() + " Set").replace("%recipient%", reciever.getName());
                sender.sendMessage(message);

                boolean advanced = false;

                Tier tier = null;

                List<ArmorSlot> slots = new ArrayList<>();

                if (args.size() >= 3) {
                    ArmorSlot slot = ArmorSlot.getSlot(args.get(2));

                    if (slot == null) {
                        if (!args.get(2).equalsIgnoreCase("full")) {
                            sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-item"));
                            return;
                        }
                    }

                    if (slot == null) {
                        slots.addAll(Arrays.asList(ArmorSlot.values()));
                    } else {
                        slots.add(slot);
                    }
                } else {
                    slots.addAll(Arrays.asList(ArmorSlot.values()));
                }

                if (args.size() >= 4) {
                    advanced = Boolean.parseBoolean(args.get(3));
                }

                if (args.size() >= 5) {
                    tier = Tiers.getByID(args.get(4));
                }

                if (args.size() >= 6) {
                    try {
                        amount = Integer.parseInt(args.get(5));
                    } catch (NumberFormatException ignored) {
                        // do nothing
                    }
                }

                for (ArmorSlot slot : slots) {
                    items.add(advanced ? set.getAdvancedItemStack(slot) : set.getItemStack(slot));
                }

                for (ItemStack item : new ArrayList<>(items)) {
                    Tier currTear = tier != null ? tier: set.getDefaultTier(ArmorSlot.getSlot(item));
                    items.remove(item);
                    ArmorUtils.setTier(item, currTear);
                    items.add(item);
                }
            }

            if (itemNamespace.equalsIgnoreCase("crystal")) {
                Tier tier = Tiers.getByID(itemKey);
                if (tier == null) {
                    sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-item"));
                    return;
                }

                String message = this.getPlugin().getLangYml().getMessage("give-success");
                message = message.replace("%item%", tier.getCrystal().getItemMeta().getDisplayName()).replace("%recipient%", reciever.getName());
                sender.sendMessage(message);
                items.add(tier.getCrystal());

                if (args.size() >= 3) {
                    try {
                        amount = Integer.parseInt(args.get(2));
                    } catch (NumberFormatException ignored) {
                        // do nothing
                    }
                }
            }

            if (itemNamespace.equalsIgnoreCase("shard")) {
                ArmorSet set = ArmorSets.getByID(itemKey);
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
                    } catch (NumberFormatException ignored) {
                        // do nothing
                    }
                }
            }

            if (items.isEmpty()) {
                sender.sendMessage(this.getPlugin().getLangYml().getMessage("invalid-item"));
                return;
            }

            for (ItemStack item : items) {
                item.setAmount(amount);
                reciever.getInventory().addItem(item);
            }
        };
    }

    @Override
    public TabCompleteHandler getTabCompleter() {
        return (sender, args) -> {

            List<String> completions = new ArrayList<>();

            if (args.isEmpty()) {
                // Currently, this case is not ever reached
                return ITEM_NAMES;
            }

            if (args.size() == 1) {
                StringUtil.copyPartialMatches(args.get(0), Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), completions);
                return completions;
            }

            if (args.size() == 2) {
                StringUtil.copyPartialMatches(args.get(1), ITEM_NAMES, completions);

                Collections.sort(completions);
                return completions;
            }

            if (args.get(1).startsWith("set:")) {
                if (args.size() == 3) {
                    StringUtil.copyPartialMatches(args.get(2), SLOTS, completions);

                    Collections.sort(completions);
                    return completions;
                }

                if (args.size() == 4) {
                    StringUtil.copyPartialMatches(args.get(3), Arrays.asList("true", "false"), completions);

                    Collections.sort(completions);
                    return completions;
                }

                if (args.size() == 5) {
                    StringUtil.copyPartialMatches(args.get(4), TIERS, completions);

                    Collections.sort(completions);
                    return completions;
                }

                if (args.size() == 6) {
                    StringUtil.copyPartialMatches(args.get(5), NUMBERS, completions);

                    return completions;
                }
            } else {
                if (args.size() == 3) {
                    StringUtil.copyPartialMatches(args.get(2), NUMBERS, completions);

                    return completions;
                }
            }

            return new ArrayList<>(0);
        };
    }
}
