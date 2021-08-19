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
