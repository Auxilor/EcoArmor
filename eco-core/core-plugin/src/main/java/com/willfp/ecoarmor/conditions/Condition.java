package com.willfp.ecoarmor.conditions;

import com.willfp.ecoarmor.EcoArmorPlugin;
import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

public abstract class Condition<T> implements Listener {
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
     * The class of the config getter type.
     */
    @Getter
    private final Class<T> typeClass;

    /**
     * Create a new condition.
     *
     * @param name      The condition name.
     * @param typeClass The class of the config type.
     */
    protected Condition(@NotNull final String name,
                        @NotNull final Class<T> typeClass) {
        this.name = name;
        this.typeClass = typeClass;

        Conditions.addNewCondition(this);
    }

    /**
     * Get if condition is met for a player.
     *
     * @param player The player.
     * @param value  The value of the condition.
     * @return If met.
     */
    public final boolean isMet(@NotNull final Player player,
                               @NotNull final Object value) {
        return isConditionMet(player, typeClass.cast(value));
    }

    protected abstract boolean isConditionMet(@NotNull Player player,
                                              @NotNull T value);

    public final void evaluateEffects(@NotNull final Player player,
                                      @NotNull final Object value,
                                      @NotNull final ArmorSet set) {
        this.getPlugin().getScheduler().runLater(() -> {
            if (isMet(player, value)) {
                for (Effect<?> effect : set.getEffects().keySet()) {
                    Object strength = set.getEffectStrength(effect);

                    if (ArmorUtils.isWearingAdvanced(player)) {
                        Object advancedStrength = set.getAdvancedEffectStrength(effect);
                        if (advancedStrength != null) {
                            strength = advancedStrength;
                        }
                    }

                    if (strength != null) {
                        effect.enable(player, strength);
                    }
                }

                set.getPotionEffects().forEach((potionEffectType, integer) -> {
                    player.addPotionEffect(new PotionEffect(potionEffectType, 0x6ffffff, integer - 1, false, false, true));
                });

                if (ArmorUtils.isWearingAdvanced(player)) {
                    set.getAdvancedPotionEffects().forEach((potionEffectType, integer) -> {
                        player.addPotionEffect(new PotionEffect(potionEffectType, 0x6ffffff, integer - 1, false, false, true));
                    });
                }
            } else {
                set.getEffects().keySet().forEach(effect -> effect.disable(player));

                for (PotionEffect effect : player.getActivePotionEffects()) {
                    if (effect.getDuration() >= 0x5ffffff && effect.getDuration() <= 0x6ffffff) {
                        player.removePotionEffect(effect.getType());
                    }
                }
            }
        }, 1);
    }
}
