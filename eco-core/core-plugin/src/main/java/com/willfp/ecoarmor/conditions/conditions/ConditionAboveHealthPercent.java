package com.willfp.ecoarmor.conditions.conditions;

import com.willfp.ecoarmor.conditions.Condition;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.jetbrains.annotations.NotNull;

public class ConditionAboveHealthPercent extends Condition<Double> {
    public ConditionAboveHealthPercent() {
        super("above-health-percent", Double.class);
    }

    @EventHandler(
            priority = EventPriority.MONITOR,
            ignoreCancelled = true
    )
    public void listener(@NotNull final EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        ArmorSet set = ArmorUtils.getSetOnPlayer(player);

        if (set == null) {
            return;
        }

        Double value = set.getConditionValue(this);

        if (value == null) {
            return;
        }

        this.getPlugin().getScheduler().runLater(() -> {
            if (isMet(player, value)) {
                set.getEffects().keySet().forEach(effect -> effect.enable(player, value));
            } else {
                set.getEffects().keySet().forEach(effect -> effect.disable(player));
            }
        }, 1);
    }

    @EventHandler(
            priority = EventPriority.MONITOR,
            ignoreCancelled = true
    )
    public void listener(@NotNull final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        ArmorSet set = ArmorUtils.getSetOnPlayer(player);

        if (set == null) {
            return;
        }

        Double value = set.getConditionValue(this);

        if (value == null) {
            return;
        }

        this.getPlugin().getScheduler().runLater(() -> {
            if (isMet(player, value)) {
                set.getEffects().keySet().forEach(effect -> effect.enable(player, value));
            } else {
                set.getEffects().keySet().forEach(effect -> effect.disable(player));
            }
        }, 1);
    }

    @Override
    public boolean isConditionMet(@NotNull final Player player,
                                  @NotNull final Double value) {
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double health = player.getHealth();

        return (health / maxHealth) * 100 >= value;
    }
}
