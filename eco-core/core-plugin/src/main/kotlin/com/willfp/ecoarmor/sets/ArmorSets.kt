package com.willfp.ecoarmor.sets;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.config.updating.ConfigUpdater;
import com.willfp.ecoarmor.EcoArmorPlugin;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@UtilityClass
public class ArmorSets {
    /**
     * Registered armor sets.
     */
    private static final BiMap<String, ArmorSet> BY_ID = HashBiMap.create();

    /**
     * Get all registered {@link ArmorSet}s.
     *
     * @return A list of all {@link ArmorSet}s.
     */
    public static List<ArmorSet> values() {
        return ImmutableList.copyOf(BY_ID.values());
    }

    /**
     * Get {@link ArmorSet} matching ID.
     *
     * @param name The name to search for.
     * @return The matching {@link ArmorSet}, or null if not found.
     */
    @Nullable
    public static ArmorSet getByID(@NotNull final String name) {
        return BY_ID.get(name);
    }

    /**
     * Update all {@link ArmorSet}s.
     *
     * @param plugin Instance of EcoArmor.
     */
    @ConfigUpdater
    public static void update(@NotNull final EcoArmorPlugin plugin) {
        for (ArmorSet set : values()) {
            removeSet(set);
        }

        for (Config setConfig : plugin.getEcoArmorYml().getSubsections("sets")) {
            new ArmorSet(setConfig, plugin);
        }
    }

    /**
     * Add new {@link ArmorSet} to EcoArmor.
     *
     * @param set The {@link ArmorSet} to add.
     */
    public static void addNewSet(@NotNull final ArmorSet set) {
        BY_ID.remove(set.getId());
        BY_ID.put(set.getId(), set);
    }

    /**
     * Remove {@link ArmorSet} from EcoArmor.
     *
     * @param set The {@link ArmorSet} to remove.
     */
    public static void removeSet(@NotNull final ArmorSet set) {
        BY_ID.remove(set.getId());
    }
}
