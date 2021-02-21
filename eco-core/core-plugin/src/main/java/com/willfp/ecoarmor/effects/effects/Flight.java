package com.willfp.ecoarmor.effects.effects;

import com.willfp.ecoarmor.effects.Effect;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Flight extends Effect<Boolean> {
    public Flight() {
        super("flight", Boolean.class);
    }

    @Override
    protected void onEnable(@NotNull final Player player) {
        player.setAllowFlight(true);
    }

    @Override
    protected void onDisable(@NotNull final Player player) {
        if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
            player.setAllowFlight(false);
        }
    }
}
