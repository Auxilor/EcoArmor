package com.willfp.ecoarmor.config;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.JsonStaticBaseConfig;
import org.jetbrains.annotations.NotNull;

public class SetsJson extends JsonStaticBaseConfig {
    /**
     * Create sets.json.
     *
     * @param plugin Instance of EcoArmor.
     */
    public SetsJson(@NotNull final EcoPlugin plugin) {
        super("sets", plugin);
    }
}
