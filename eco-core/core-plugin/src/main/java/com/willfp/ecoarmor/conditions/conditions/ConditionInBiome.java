package com.willfp.ecoarmor.conditions.conditions;

import com.willfp.ecoarmor.conditions.Condition;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ConditionInBiome extends Condition<String> {
    public ConditionInBiome() {
        super("in-biome", String.class);
    }

    @EventHandler(
            priority = EventPriority.MONITOR,
            ignoreCancelled = true
    )
    public void listener(@NotNull final PlayerMoveEvent event) {
        Player player = event.getPlayer();

        ArmorSet set = ArmorUtils.getSetOnPlayer(player);

        if (set == null) {
            return;
        }

        String value = set.getConditionValue(this);

        if (value == null) {
            return;
        }

        evaluateEffects(player, value, set);
    }

    @Override
    public boolean isConditionMet(@NotNull final Player player,
                                  @NotNull final String value) {
        List<String> biomeNames = Arrays.asList(value.toLowerCase().split(" "));
        Biome biome = player.getLocation().getWorld().getBiome(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
        return biomeNames.contains(biome.name().toLowerCase());
    }
}
