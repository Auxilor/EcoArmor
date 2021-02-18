package com.willfp.ecoarmor.config;

import com.willfp.eco.util.config.StaticOptionalConfig;
import com.willfp.ecoarmor.EcoArmorPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class CustomConfig extends StaticOptionalConfig {
    /**
     * Create new ArmorSet config.
     *
     * @param configName The name of the config.
     * @param config     The config.
     */
    public CustomConfig(@NotNull final String configName,
                        @NotNull final YamlConfiguration config) {
        super(configName, EcoArmorPlugin.getInstance(), config);
    }
}
