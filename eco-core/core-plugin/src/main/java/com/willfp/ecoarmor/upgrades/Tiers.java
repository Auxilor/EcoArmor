package com.willfp.ecoarmor.upgrades;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.config.updating.ConfigUpdater;
import com.willfp.ecoarmor.EcoArmorPlugin;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@UtilityClass
public class Tiers {
    /**
     * Registered tiers.
     */
    private static final BiMap<String, Tier> BY_ID = HashBiMap.create();

    /**
     * Default tier.
     */
    @Getter
    private static Tier defaultTier;

    /**
     * Get {@link Tiers} matching ID.
     *
     * @param name The name to search for.
     * @return The matching {@link Tiers}, or null if not found.
     */
    @Nullable
    public static Tier getByID(@Nullable final String name) {
        return BY_ID.get(name);
    }

    /**
     * Get all registered {@link Tiers}s.
     *
     * @return A list of all {@link Tiers}s.
     */
    public static List<Tier> values() {
        return ImmutableList.copyOf(BY_ID.values());
    }

    /**
     * Add new {@link Tier} to EcoArmor.
     *
     * @param tier The {@link Tier} to add.
     */
    public static void addNewTier(@NotNull final Tier tier) {
        BY_ID.remove(tier.getId());
        BY_ID.put(tier.getId(), tier);
    }

    /**
     * Update.
     *
     * @param plugin Instance of EcoArmor.
     */
    @ConfigUpdater
    public static void reload(@NotNull final EcoArmorPlugin plugin) {
        BY_ID.clear();

        for (Config tierConfig : plugin.getEcoArmorYml().getSubsections("tiers")) {
            new Tier(tierConfig, plugin);
        }

        defaultTier = Tiers.getByID("default");
    }

    static {
        reload(EcoArmorPlugin.getInstance());
    }
}
