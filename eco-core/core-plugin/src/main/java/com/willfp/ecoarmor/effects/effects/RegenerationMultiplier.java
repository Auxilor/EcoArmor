package com.willfp.ecoarmor.effects.effects;

import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.jetbrains.annotations.NotNull;

public class RegenerationMultiplier extends Effect {
    public RegenerationMultiplier() {
        super("regeneration-multiplier");
    }

    @EventHandler
    public void onDamage(@NotNull final EntityRegainHealthEvent event) {
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

        event.setAmount(event.getAmount() * multiplier);
    }
}
