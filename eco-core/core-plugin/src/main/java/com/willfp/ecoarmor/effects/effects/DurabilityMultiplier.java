package com.willfp.ecoarmor.effects.effects;

import com.willfp.eco.util.NumberUtils;
import com.willfp.ecoarmor.effects.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.jetbrains.annotations.NotNull;

public class DurabilityMultiplier extends Effect<Double> {
    public DurabilityMultiplier() {
        super("durability-multiplier", Double.class);
    }

    @EventHandler
    public void listener(@NotNull final PlayerItemDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();

        Double multiplier = this.getStrengthForPlayer(player);

        if (multiplier == null) {
            return;
        }

        if (NumberUtils.randFloat(0, 100) < 1 - (1 / multiplier)) {
            event.setCancelled(true);
        }
    }
}
