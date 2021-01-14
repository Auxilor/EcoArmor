package com.willfp.ecoarmor.effects;

import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public abstract class Effect implements Listener {
    /**
     * Instance of EcoArmor.
     */
    @Getter(AccessLevel.PROTECTED)
    private final AbstractEcoPlugin plugin = AbstractEcoPlugin.getInstance();

    /**
     * The name of the effect.
     */
    @Getter
    private final String name;

    protected Effect(@NotNull final String name) {
        this.name = name;

        Effects.addNewEffect(this);
    }
}
