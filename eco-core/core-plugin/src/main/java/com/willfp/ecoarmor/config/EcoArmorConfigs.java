package com.willfp.ecoarmor.config;

import com.willfp.eco.util.config.updating.annotations.ConfigUpdater;
import com.willfp.ecoarmor.config.configs.Sets;
import com.willfp.ecoarmor.config.configs.Tiers;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EcoArmorConfigs {
    /**
     * sets.yml.
     */
    public static final Sets SETS = new Sets();


    /**
     * tiers.yml.
     */
    public static final Tiers TIERS = new Tiers();

    @ConfigUpdater
    public static void update() {
        SETS.clearCache();
        TIERS.clearCache();
    }
}
