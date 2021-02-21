package com.willfp.ecoarmor.effects.effects;

import com.willfp.eco.util.NumberUtils;
import com.willfp.ecoarmor.effects.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class EvadeChance extends Effect<Double> {
    public EvadeChance() {
        super("evade-chance", Double.class);
    }

    @EventHandler
    public void listener(@NotNull final EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        Double chance = this.getStrengthForPlayer(player);

        if (chance == null) {
            return;
        }

        if (NumberUtils.randFloat(0, 100) < chance) {
            event.setCancelled(true);
        }
    }
}
