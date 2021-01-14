package com.willfp.ecoarmor.commands;

import com.willfp.eco.util.command.AbstractCommand;
import com.willfp.eco.util.command.AbstractTabCompleter;
import com.willfp.eco.util.config.updating.annotations.ConfigUpdater;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.ArmorSets;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TabcompleterEagive extends AbstractTabCompleter {
    /**
     * The cached enchantment names.
     */
    private static final List<String> SET_NAMES = ArmorSets.values().stream().map(ArmorSet::getName).collect(Collectors.toList());

    /**
     * Instantiate a new tab-completer for /eagive.
     */
    public TabcompleterEagive() {
        super((AbstractCommand) Objects.requireNonNull(Bukkit.getPluginCommand("eagive")).getExecutor());
    }

    /**
     * Called on reload.
     */
    @ConfigUpdater
    public static void reload() {
        SET_NAMES.clear();
        SET_NAMES.addAll(ArmorSets.values().stream().map(ArmorSet::getName).collect(Collectors.toList()));
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

        return new ArrayList<>(0);
    }
}
