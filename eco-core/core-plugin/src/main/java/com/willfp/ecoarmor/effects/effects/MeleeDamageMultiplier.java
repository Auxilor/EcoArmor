package com.willfp.ecoarmor.effects.effects;

import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class MeleeDamageMultiplier extends Effect<Double> {
    public MeleeDamageMultiplier() {
        super("melee-damage-multiplier", Double.class);
    }

    @EventHandler
    public void listener(@NotNull final EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player attacker = (Player) event.getDamager();

        Double multiplier = ArmorUtils.getEffectStrength(attacker, this);
        if (multiplier == null) {
            return;
        }

        event.setDamage(event.getDamage() * multiplier);
    }
}
