package com.willfp.ecoarmor.mcmmo;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.willfp.ecoarmor.effects.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class McmmoXpMultiplier extends Effect<Double> {
    public McmmoXpMultiplier() {
        super("mcmmo-xp-multiplier", Double.class);
    }

    @EventHandler
    public void listener(@NotNull final McMMOPlayerXpGainEvent event) {
        Player player = event.getPlayer().getPlayer();

        Double multiplier = this.getStrengthForPlayer(player);

        if (multiplier == null) {
            return;
        }

        event.setRawXpGained((float) (event.getRawXpGained() * multiplier));
    }
}
