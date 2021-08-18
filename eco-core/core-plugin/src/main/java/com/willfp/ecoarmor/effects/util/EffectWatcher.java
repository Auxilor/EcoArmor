package com.willfp.ecoarmor.effects.util;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.events.ArmorChangeEvent;
import com.willfp.eco.core.events.ArmorEquipEvent;
import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.effects.Effects;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

public class EffectWatcher extends PluginDependent<EcoPlugin> implements Listener {
    /**
     * Pass an {@link EcoPlugin} in order to interface with it.
     *
     * @param plugin The plugin to manage.
     */
    public EffectWatcher(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    /**
     * Listener for armor equipping.
     *
     * @param event The event to listen for.
     */
    @EventHandler
    public void armorEquipListener(@NotNull final ArmorChangeEvent event) {
        Player player = event.getPlayer();
        ArmorSet set = ArmorUtils.getSetOnPlayer(player);

        boolean conditionsMet = ArmorUtils.areConditionsMet(player);

        for (Effect<?> effect : Effects.values()) {
            boolean enabled = true;

            if (set == null) {
                effect.disable(player);
                continue;
            }

            Object strength = set.getEffectStrength(effect);

            if (ArmorUtils.isWearingAdvanced(player)) {
                Object advancedStrength = set.getAdvancedEffectStrength(effect);
                if (advancedStrength != null) {
                    strength = advancedStrength;
                }
            }

            if (strength == null) {
                enabled = false;
            }

            if (!conditionsMet) {
                enabled = false;
            }

            if (enabled) {
                effect.enable(player, strength);
            } else {
                effect.disable(player);
            }
        }

        if (set == null || !conditionsMet) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getDuration() >= 0x5ffffff && effect.getDuration() <= 0x6ffffff) {
                    player.removePotionEffect(effect.getType());
                }
            }
        } else {
            set.getPotionEffects().forEach((potionEffectType, integer) -> {
                player.addPotionEffect(new PotionEffect(potionEffectType, 0x6ffffff, integer - 1, false, false, true));
            });

            if (ArmorUtils.isWearingAdvanced(player)) {
                set.getAdvancedPotionEffects().forEach((potionEffectType, integer) -> {
                    player.addPotionEffect(new PotionEffect(potionEffectType, 0x6ffffff, integer - 1, false, false, true));
                });
            }
        }
    }
}
