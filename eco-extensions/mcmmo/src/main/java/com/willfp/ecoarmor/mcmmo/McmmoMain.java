package com.willfp.ecoarmor.mcmmo;

import com.willfp.eco.util.extensions.Extension;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.ecoarmor.effects.Effect;
import org.jetbrains.annotations.NotNull;

public class McmmoMain extends Extension {
    public static final Effect<?> MCMMO_XP_MULTIPLIER = new McmmoXpMultiplier();

    /**
     * Instantiate fossil extension.
     *
     * @param plugin Instance of Talismans.
     */
    public McmmoMain(@NotNull final AbstractEcoPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
