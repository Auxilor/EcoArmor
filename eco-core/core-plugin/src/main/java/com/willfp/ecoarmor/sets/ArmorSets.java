package com.willfp.ecoarmor.sets;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.willfp.eco.core.config.ConfigUpdater;
import com.willfp.ecoarmor.EcoArmorPlugin;
import com.willfp.ecoarmor.config.BaseEcoArmorConfig;
import com.willfp.ecoarmor.config.CustomConfig;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class ArmorSets {
    /**
     * Registered armor sets.
     */
    private static final BiMap<String, ArmorSet> BY_NAME = HashBiMap.create();

    /**
     * Sets that exist by default.
     */
    private static final List<String> DEFAULT_SET_NAMES = Arrays.asList(
            "miner",
            "reaper",
            "ender",
            "young"
    );

    /**
     * Get all registered {@link ArmorSet}s.
     *
     * @return A list of all {@link ArmorSet}s.
     */
    public static List<ArmorSet> values() {
        return ImmutableList.copyOf(BY_NAME.values());
    }

    /**
     * Get {@link ArmorSet} matching name.
     *
     * @param name The name to search for.
     * @return The matching {@link ArmorSet}, or null if not found.
     */
    @Nullable
    public static ArmorSet getByName(@NotNull final String name) {
        return BY_NAME.get(name);
    }

    /**
     * Update all {@link ArmorSet}s.
     */
    @ConfigUpdater
    public static void update() {
        for (ArmorSet set : values()) {
            removeSet(set);
        }

        for (String defaultSetName : DEFAULT_SET_NAMES) {
            new ArmorSet(defaultSetName, new BaseEcoArmorConfig(defaultSetName));
        }

        try {
            Files.walk(Paths.get(new File(EcoArmorPlugin.getInstance().getDataFolder(), "sets/").toURI()))
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        String name = path.getFileName().toString().replace(".yml", "");
                        new ArmorSet(
                                name,
                                new CustomConfig(name, YamlConfiguration.loadConfiguration(path.toFile()))
                        );
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add new {@link ArmorSet} to EcoArmor.
     *
     * @param set The {@link ArmorSet} to add.
     */
    public static void addNewSet(@NotNull final ArmorSet set) {
        BY_NAME.remove(set.getName());
        BY_NAME.put(set.getName(), set);
    }

    /**
     * Remove {@link ArmorSet} from EcoArmor.
     *
     * @param set The {@link ArmorSet} to remove.
     */
    public static void removeSet(@NotNull final ArmorSet set) {
        BY_NAME.remove(set.getName());
    }
}
