package com.willfp.ecoarmor.effects.util;

import com.willfp.eco.util.events.armorequip.ArmorEquipEvent;
import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.effects.Effects;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class EffectWatcher extends PluginDependent implements Listener {
    /**
     * Pass an {@link AbstractEcoPlugin} in order to interface with it.
     *
     * @param plugin The plugin to manage.
     */
    public EffectWatcher(@NotNull final AbstractEcoPlugin plugin) {
        super(plugin);
    }

    /**
     * Listener for armor equipping.
     *
     * @param event The event to listen for.
     */
    @EventHandler
    public void armorEquipListener(@NotNull final ArmorEquipEvent event) {
        Player player = event.getPlayer();

        this.getPlugin().getScheduler().runLater(() -> {
            ArmorSet set = ArmorUtils.getSetOnPlayer(player);

            for (Effect<?> effect : Effects.values()) {
                boolean enabled = true;

                if (set == null) {
                    enabled = false;
                } else {
                    if (set.getEffectStrength(effect) == null) {
                        enabled = false;
                    }

                    if (!ArmorUtils.areConditionsMet(player)) {
                        enabled = false;
                    }
                }

                if (enabled) {
                    effect.enable(player);
                } else {
                    effect.disable(player);
                }
            }
        }, 1);
    }
}
