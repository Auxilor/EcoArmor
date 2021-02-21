package com.willfp.ecoarmor.effects.effects;

import com.willfp.eco.util.events.naturalexpgainevent.NaturalExpGainEvent;
import com.willfp.ecoarmor.effects.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class ExperienceMultiplier extends Effect<Double> {
    public ExperienceMultiplier() {
        super("experience-multiplier", Double.class);
    }

    @EventHandler
    public void listener(@NotNull final NaturalExpGainEvent event) {
        Player player = event.getExpChangeEvent().getPlayer();

        if (event.getExpChangeEvent().getAmount() < 0) {
            return;
        }

        Double multiplier = this.getStrengthForPlayer(player);

        if (multiplier == null) {
            return;
        }

        event.getExpChangeEvent().setAmount((int) Math.ceil(event.getExpChangeEvent().getAmount() * multiplier));
    }
}
