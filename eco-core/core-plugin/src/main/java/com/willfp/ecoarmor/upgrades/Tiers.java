package com.willfp.ecoarmor.upgrades;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.willfp.eco.util.config.updating.annotations.ConfigUpdater;
import com.willfp.ecoarmor.EcoArmorPlugin;
import com.willfp.ecoarmor.config.BaseTierConfig;
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
public class Tiers {
    /**
     * Instance of EcoArmor.
     */
    private static final EcoArmorPlugin PLUGIN = EcoArmorPlugin.getInstance();

    /**
     * Registered tiers.
     */
    private static final BiMap<String, Tier> BY_NAME = HashBiMap.create();

    /**
     * Tiers that exist by default.
     */
    private static final List<String> DEFAULT_TIER_NAMES = Arrays.asList(
            "iron",
            "diamond",
            "netherite",
            "manyullyn",
            "cobalt",
            "osmium",
            "exotic"
    );

    /**
     * Default tier.
     */
    public static final Tier DEFAULT = new Tier("default", new BaseTierConfig("default"), PLUGIN);

    /**
     * Get {@link Tiers} matching name.
     *
     * @param name The name to search for.
     * @return The matching {@link Tiers}, or null if not found.
     */
    @Nullable
    public static Tier getByName(@Nullable final String name) {
        return BY_NAME.get(name);
    }

    /**
     * Get all registered {@link Tiers}s.
     *
     * @return A list of all {@link Tiers}s.
     */
    public static List<Tier> values() {
        return ImmutableList.copyOf(BY_NAME.values());
    }

    /**
     * Add new {@link Tier} to EcoArmor.
     *
     * @param tier The {@link Tier} to add.
     */
    public static void addNewTier(@NotNull final Tier tier) {
        BY_NAME.remove(tier.getName());
        BY_NAME.put(tier.getName(), tier);
    }

    /**
     * Update.
     */
    @ConfigUpdater
    public static void reload() {
        BY_NAME.clear();

        for (String defaultSetName : DEFAULT_TIER_NAMES) {
            new Tier(defaultSetName, new BaseTierConfig(defaultSetName), PLUGIN);
        }

        try {
            Files.walk(Paths.get(new File(EcoArmorPlugin.getInstance().getDataFolder(), "tiers/").toURI()))
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        String name = path.getFileName().toString().replace(".yml", "");
                        new Tier(
                                name,
                                new CustomConfig(name, YamlConfiguration.loadConfiguration(path.toFile())),
                                PLUGIN
                        );
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        reload();
    }
}
