package com.willfp.ecoarmor.conditions.conditions;

import com.willfp.ecoarmor.conditions.Condition;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.jetbrains.annotations.NotNull;

public class ConditionBelowXPLevel extends Condition<Integer> {
    public ConditionBelowXPLevel() {
        super("below-xp-level", Integer.class);
    }

    @EventHandler(
            priority = EventPriority.MONITOR,
            ignoreCancelled = true
    )
    public void listener(@NotNull final PlayerExpChangeEvent event) {
        Player player = event.getPlayer();

        ArmorSet set = ArmorUtils.getSetOnPlayer(player);

        if (set == null) {
            return;
        }

        Integer value = set.getConditionValue(this);

        if (value == null) {
            return;
        }

        evaluateEffects(player, value, set);
    }

    @Override
    public boolean isConditionMet(@NotNull final Player player,
                                  @NotNull final Integer value) {
        return player.getLevel() < value;
    }
}
