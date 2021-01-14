package com.willfp.ecoarmor.sets;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.willfp.eco.util.config.updating.annotations.ConfigUpdater;
import com.willfp.ecoarmor.config.EcoArmorConfigs;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@UtilityClass
public class ArmorSets {
    private static final BiMap<String, ArmorSet> BY_NAME = HashBiMap.create();

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

        for (String key : EcoArmorConfigs.SETS.getConfig().getKeys(false)) {
            new ArmorSet(key);
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
