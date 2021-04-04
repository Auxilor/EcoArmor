package com.willfp.ecoarmor.config;

import com.willfp.eco.core.config.YamlConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class CustomConfig extends YamlConfig {
    /**
     * The config name.
     */
    private final String configName;

    /**
     * Create new custom config.
     *
     * @param configName The name of the config.
     * @param config     The config.
     */
    public CustomConfig(@NotNull final String configName,
                        @NotNull final YamlConfiguration config) {
        super(config);
        this.configName = configName;
    }
}
