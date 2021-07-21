package com.willfp.ecoarmor.config;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.json.JSONStaticBaseConfig;
import org.jetbrains.annotations.NotNull;

public class EcoArmorJson extends JSONStaticBaseConfig {
    /**
     * Create tiers.json.
     *
     * @param plugin Instance of EcoArmor.
     */
    public EcoArmorJson(@NotNull final EcoPlugin plugin) {
        super("ecoarmor", plugin);
    }
}
