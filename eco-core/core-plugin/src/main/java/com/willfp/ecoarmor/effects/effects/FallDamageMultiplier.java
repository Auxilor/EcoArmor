package com.willfp.ecoarmor.effects.effects;

import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class FallDamageMultiplier extends Effect<Double> {
    public FallDamageMultiplier() {
        super("fall-damage-multiplier", Double.class);
    }

    @EventHandler
    public void listener(@NotNull final EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (!this.isEnabledForPlayer(player)) {
            return;
        }

        Double multiplier = ArmorUtils.getEffectStrength(player, this);
        if (multiplier == null) {
            return;
        }

        event.setDamage(event.getDamage() * multiplier);
    }
}
