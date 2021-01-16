package com.willfp.ecoarmor.effects.effects;

import com.willfp.eco.util.NumberUtils;
import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.jetbrains.annotations.NotNull;

public class DurabilityMultiplier extends Effect<Double> {
    public DurabilityMultiplier() {
        super("durability-multiplier");
    }

    @EventHandler
    public void onDamage(@NotNull final PlayerItemDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();

        double multiplier = ArmorUtils.getEffectStrength(player, this);

        if (multiplier == 0) {
            return;
        }

        if (NumberUtils.randFloat(0, 100) < 1 - (1 / multiplier)) {
            event.setCancelled(true);
        }
    }
}
