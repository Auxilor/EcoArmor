package com.willfp.ecoarmor.conditions.conditions;

import com.willfp.ecoarmor.conditions.Condition;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ConditionInWorld extends Condition<String> {
    public ConditionInWorld() {
        super("in-world", String.class);
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

        this.getPlugin().getScheduler().runLater(() -> {
            if (isMet(player, value)) {
                set.getEffects().keySet().forEach(effect -> effect.enable(player, value));
            } else {
                set.getEffects().keySet().forEach(effect -> effect.disable(player));
            }
        }, 1);
    }

    @Override
    public boolean isConditionMet(@NotNull final Player player,
                                  @NotNull final String value) {
        List<String> worldNames = Arrays.asList(value.toLowerCase().split(" "));
        World world = player.getLocation().getWorld();
        if (world == null) {
            return false;
        }

        return worldNames.contains(world.getName().toLowerCase());
    }
}
