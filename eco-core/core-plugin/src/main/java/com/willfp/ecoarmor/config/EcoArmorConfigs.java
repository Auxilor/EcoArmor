package com.willfp.ecoarmor.config;

import com.willfp.eco.util.config.updating.annotations.ConfigUpdater;
import com.willfp.ecoarmor.config.configs.Sets;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EcoArmorConfigs {
    public static final Sets SETS = new Sets();

    /**
     * Update all configs.
     */
    @ConfigUpdater
    public void updateConfigs() {
        SETS.update();
    }
}
