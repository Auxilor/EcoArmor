package com.willfp.ecoarmor.conditions.conditions;

import com.willfp.ecoarmor.conditions.Condition;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class ConditionBelowY extends Condition<Double> {
    public ConditionBelowY() {
        super("below-y", Double.class);
    }

    @EventHandler
    public void listener(@NotNull final PlayerMoveEvent event) {
        Player player = event.getPlayer();

        ArmorSet set = ArmorUtils.getSetOnPlayer(player);

        if (set == null) {
            return;
        }

        Double value = set.getConditionValue(this);

        if (value == null) {
            return;
        }

        if (isMet(player, value)) {
            set.getEffects().keySet().forEach(effect -> effect.enable(player));
        } else {
            set.getEffects().keySet().forEach(effect -> effect.disable(player));
        }
    }

    @Override
    public boolean isConditionMet(@NotNull final Player player,
                                  @NotNull final Double value) {
        return player.getLocation().getY() < value;
    }
}
