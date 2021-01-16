package com.willfp.ecoarmor.effects.effects;

import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;

public class DamageMultiplier extends Effect {
    public DamageMultiplier() {
        super("damage-multiplier", ValueType.DOUBLE);
    }

    @EventHandler
    public void onDamage(@NotNull final EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player attacker = null;
        if (event.getDamager() instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) event.getDamager()).getShooter();
            if (shooter == null) {
                return;
            }

            if (shooter instanceof Player) {
                attacker = (Player) shooter;
            }
        } else if (event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        }

        if (attacker == null) {
            return;
        }

        double multiplier = ArmorUtils.getEffectStrength(attacker, this);
        if (multiplier == 0) {
            return;
        }

        event.setDamage(event.getDamage() * multiplier);
    }
}
