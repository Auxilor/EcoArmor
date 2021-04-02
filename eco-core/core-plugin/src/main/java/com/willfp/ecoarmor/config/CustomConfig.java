package com.willfp.ecoarmor.config;

import com.willfp.eco.util.config.StaticOptionalConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class CustomConfig extends StaticOptionalConfig {
    /**
     * Create new custom config.
     *
     * @param configName The name of the config.
     * @param config     The config.
     */
    public CustomConfig(@NotNull final String configName,
                        @NotNull final YamlConfiguration config) {
        super(configName, config);
    }
}
