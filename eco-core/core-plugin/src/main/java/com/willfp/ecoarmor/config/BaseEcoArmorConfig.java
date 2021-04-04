package com.willfp.ecoarmor.config;

import com.willfp.eco.core.config.ExtendableConfig;
import com.willfp.ecoarmor.EcoArmorPlugin;
import org.jetbrains.annotations.NotNull;

public class BaseEcoArmorConfig extends ExtendableConfig {
    /**
     * Create new ArmorSet config.
     *
     * @param configName The name of the config.
     */
    public BaseEcoArmorConfig(@NotNull final String configName) {
        super(configName, true, EcoArmorPlugin.getInstance(), EcoArmorPlugin.class, "sets/");
    }
}
