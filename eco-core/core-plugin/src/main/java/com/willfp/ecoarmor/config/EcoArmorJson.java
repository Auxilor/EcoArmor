package com.willfp.ecoarmor.config;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.JsonStaticBaseConfig;
import org.jetbrains.annotations.NotNull;

public class EcoArmorJson extends JsonStaticBaseConfig {
    /**
     * Create tiers.json.
     *
     * @param plugin Instance of EcoArmor.
     */
    public EcoArmorJson(@NotNull final EcoPlugin plugin) {
        super("ecoarmor", plugin);
    }
}
