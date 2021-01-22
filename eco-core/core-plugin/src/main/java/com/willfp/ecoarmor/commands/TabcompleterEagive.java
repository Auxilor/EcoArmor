package com.willfp.ecoarmor.commands;

import com.willfp.eco.util.command.AbstractCommand;
import com.willfp.eco.util.command.AbstractTabCompleter;
import com.willfp.eco.util.config.updating.annotations.ConfigUpdater;
import com.willfp.ecoarmor.sets.ArmorSets;
import com.willfp.ecoarmor.sets.meta.ArmorSlot;
import com.willfp.ecoarmor.upgrades.crystal.UpgradeCrystal;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TabcompleterEagive extends AbstractTabCompleter {
    /**
     * The cached names.
     */
    private static final List<String> SET_NAMES = new ArrayList<>();

    /**
     * The cached slots.
     */
    private static final List<String> SLOTS = new ArrayList<>();

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
     * Instantiate a new tab-completer for /eagive.
     */
    public TabcompleterEagive() {
        super((AbstractCommand) Objects.requireNonNull(Bukkit.getPluginCommand("eagive")).getExecutor());
        reload();
    }

    /**
     * Called on reload.
     */
    @ConfigUpdater
    public static void reload() {
        SET_NAMES.clear();
        SET_NAMES.addAll(ArmorSets.values().stream().map(armorSet -> "set:" + armorSet.getName()).collect(Collectors.toList()));
        SET_NAMES.addAll(ArmorSets.values().stream().map(armorSet -> "shard:" + armorSet.getName()).collect(Collectors.toList()));
        SET_NAMES.addAll(UpgradeCrystal.values().stream().map(crystal -> "crystal:" + crystal.getTier()).collect(Collectors.toList()));
        SLOTS.addAll(Arrays.stream(ArmorSlot.values()).map(slot -> slot.name().toLowerCase()).collect(Collectors.toList()));
        SLOTS.add("full");
    }

    /**
     * The execution of the tabcompleter.
     *
     * @param sender The sender of the command.
     * @param args   The arguments of the command.
     * @return A list of tab-completions.
     */
    @Override
    public List<String> onTab(@NotNull final CommandSender sender,
                              @NotNull final List<String> args) {
        List<String> completions = new ArrayList<>();

        if (args.isEmpty()) {
            // Currently, this case is not ever reached
            return SET_NAMES;
        }

        if (args.size() == 1) {
            StringUtil.copyPartialMatches(args.get(0), Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), completions);
            return completions;
        }

        if (args.size() == 2) {
            StringUtil.copyPartialMatches(args.get(1), SET_NAMES, completions);

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
                StringUtil.copyPartialMatches(args.get(4), NUMBERS, completions);

                return completions;
            }
        } else {
            if (args.size() == 3) {
                StringUtil.copyPartialMatches(args.get(2), NUMBERS, completions);

                return completions;
            }
        }

        return new ArrayList<>(0);
    }
}
