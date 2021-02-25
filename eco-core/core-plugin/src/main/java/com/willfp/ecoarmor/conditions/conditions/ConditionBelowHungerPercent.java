package com.willfp.ecoarmor.conditions.conditions;

import com.willfp.ecoarmor.conditions.Condition;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.jetbrains.annotations.NotNull;

public class ConditionBelowHungerPercent extends Condition<Double> {
    public ConditionBelowHungerPercent() {
        super("below-hunger-percent", Double.class);
    }

    @EventHandler(
            priority = EventPriority.MONITOR,
            ignoreCancelled = true
    )
    public void listener(@NotNull final FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();

        ArmorSet set = ArmorUtils.getSetOnPlayer(player);

        if (set == null) {
            return;
        }

        Double value = set.getConditionValue(this);

        if (value == null) {
            return;
        }

        evaluateEffects(player, value, set);
    }

    @Override
    public boolean isConditionMet(@NotNull final Player player,
                                  @NotNull final Double value) {
        double maxFood = 20;
        double food = player.getFoodLevel();

        return (food / maxFood) * 100 < value;
    }
}
