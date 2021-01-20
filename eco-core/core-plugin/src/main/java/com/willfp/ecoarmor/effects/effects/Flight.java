package com.willfp.ecoarmor.effects.effects;

import com.willfp.eco.util.events.armorequip.ArmorEquipEvent;
import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class Flight extends Effect<Boolean> {
    public Flight() {
        super("flight");
    }

    @EventHandler
    public void onArmorEquip(@NotNull final ArmorEquipEvent event) {
        Player player = event.getPlayer();

        this.getPlugin().getScheduler().runLater(() -> {
            Boolean flight = ArmorUtils.getEffectStrength(player, this);
            if (flight == null) {
                if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                    player.setAllowFlight(false);
                }
            } else {
                if (flight) {
                    player.setAllowFlight(true);
                }
            }
        }, 1);
    }
}
