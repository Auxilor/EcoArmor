package com.willfp.ecoarmor.effects.effects;

import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class DamageTakenMultiplier extends Effect {
    public DamageTakenMultiplier() {
        super("damage-taken-multiplier", ValueType.DOUBLE);
    }

    @EventHandler
    public void onDamage(@NotNull final EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        double multiplier = ArmorUtils.getEffectStrength(player, this);
        if (multiplier == 0) {
            return;
        }

        event.setDamage(event.getDamage() * multiplier);
    }
}
