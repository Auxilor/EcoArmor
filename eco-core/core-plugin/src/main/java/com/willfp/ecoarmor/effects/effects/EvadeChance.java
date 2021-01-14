package com.willfp.ecoarmor.effects.effects;

import com.willfp.eco.util.NumberUtils;
import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class EvadeChance extends Effect {
    public EvadeChance() {
        super("evade-chance");
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

        double chance = ArmorUtils.getEffectStrength(player, this);

        if (NumberUtils.randFloat(0, 100) < chance) {
            event.setCancelled(true);
        }
    }
}
