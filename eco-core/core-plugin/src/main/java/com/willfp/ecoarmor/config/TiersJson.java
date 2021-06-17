package com.willfp.ecoarmor.config;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.JsonStaticBaseConfig;
import org.jetbrains.annotations.NotNull;

public class TiersJson extends JsonStaticBaseConfig {
    /**
     * Create tiers.json.
     *
     * @param plugin Instance of EcoArmor.
     */
    public TiersJson(@NotNull final EcoPlugin plugin) {
        super("tiers", plugin);
    }
}
