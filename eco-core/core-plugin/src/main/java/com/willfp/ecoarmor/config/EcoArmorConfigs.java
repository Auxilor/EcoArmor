package com.willfp.ecoarmor.config;

import com.willfp.eco.util.config.updating.annotations.ConfigUpdater;
import com.willfp.ecoarmor.config.configs.Tiers;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EcoArmorConfigs {
    /**
     * tiers.yml.
     */
    public static final Tiers TIERS = new Tiers();

    @ConfigUpdater
    public static void update() {
        TIERS.clearCache();
    }
}
