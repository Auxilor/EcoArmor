package com.willfp.ecoarmor.effects.effects;

import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;

public class TridentDamageMultiplier extends Effect<Double> {
    public TridentDamageMultiplier() {
        super("trident-damage-multiplier", Double.class);
    }

    @EventHandler
    public void listener(@NotNull final EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player attacker = null;
        if (event.getDamager() instanceof Trident) {
            ProjectileSource shooter = ((Projectile) event.getDamager()).getShooter();
            if (shooter == null) {
                return;
            }

            if (shooter instanceof Player) {
                attacker = (Player) shooter;
            }
        }

        if (attacker == null) {
            return;
        }

        Double multiplier = ArmorUtils.getEffectStrength(attacker, this);

        if (multiplier == null) {
            return;
        }

        if (multiplier == 0) {
            return;
        }

        event.setDamage(event.getDamage() * multiplier);
    }
}
