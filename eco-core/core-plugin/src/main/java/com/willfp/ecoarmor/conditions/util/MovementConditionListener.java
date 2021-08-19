package com.willfp.ecoarmor.conditions.util;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.willfp.eco.core.PluginDependent;
import com.willfp.ecoarmor.EcoArmorPlugin;
import com.willfp.ecoarmor.conditions.Condition;
import com.willfp.ecoarmor.conditions.Conditions;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.net.http.WebSocket;
import java.util.Set;

public class MovementConditionListener extends PluginDependent<EcoArmorPlugin> implements Listener {
    /**
     * Pass an {@link EcoArmorPlugin} in order to interface with it.
     *
     * @param plugin The plugin to manage.
     */
    public MovementConditionListener(@NotNull EcoArmorPlugin plugin) {
        super(plugin);
    }

    private static final Set<Condition<?>> CONDITIONS = ImmutableSet.of(
            Conditions.ABOVE_Y,
            Conditions.IN_BIOME,
            Conditions.BELOW_Y,
            Conditions.IN_WATER,
            Conditions.IN_WORLD
    );

    @EventHandler(
            priority = EventPriority.MONITOR,
            ignoreCancelled = true
    )
    public void listener(@NotNull final PlayerMoveEvent event) {
        Player player = event.getPlayer();

        ArmorSet set = ArmorUtils.getSetOnPlayer(player);

        if (set == null) {
            return;
        }

        for (Condition<?> condition : CONDITIONS) {
            Object value = set.getConditionValue(condition);

            if (value == null) {
                continue;
            }

            condition.evaluateEffects(player, value, set);
        }
    }
}
