package com.willfp.ecoarmor.conditions.conditions;

import com.willfp.ecoarmor.conditions.Condition;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ConditionHasPermission extends Condition<String> implements Runnable {
    public ConditionHasPermission() {
        super("has-permission", String.class);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
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
    }

    @Override
    public boolean isConditionMet(@NotNull final Player player,
                                  @NotNull final String value) {
        return player.hasPermission(value);
    }
}
