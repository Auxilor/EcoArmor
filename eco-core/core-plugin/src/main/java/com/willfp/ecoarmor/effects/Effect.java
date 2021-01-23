package com.willfp.ecoarmor.effects;

import com.willfp.ecoarmor.EcoArmorPlugin;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public abstract class Effect<T> implements Listener {
    /**
     * Instance of EcoArmor.
     */
    @Getter(AccessLevel.PROTECTED)
    private final EcoArmorPlugin plugin = EcoArmorPlugin.getInstance();

    /**
     * The name of the effect.
     */
    @Getter
    private final String name;

    /**
     * If the effect is enabled.
     */
    @Getter
    private boolean enabled;

    /**
     * The class of the config getter type.
     */
    @Getter
    private final Class<T> typeClass;

    /**
     * Create a new effect.
     *
     * @param name      The effect name.
     * @param typeClass The class of the config type.
     */
    protected Effect(@NotNull final String name,
                     @NotNull final Class<T> typeClass) {
        this.name = name;
        this.typeClass = typeClass;

        update();
        Effects.addNewEffect(this);
    }

    /**
     * Update if the effect is enabled.
     */
    public void update() {
        enabled = this.getPlugin().getConfigYml().getBool("effects." + name + ".enabled");
    }
}
