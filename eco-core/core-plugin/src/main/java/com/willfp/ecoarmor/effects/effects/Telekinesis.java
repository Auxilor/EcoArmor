package com.willfp.ecoarmor.effects.effects;

import com.willfp.eco.util.NumberUtils;
import com.willfp.eco.util.TelekinesisUtils;
import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Telekinesis extends Effect<Boolean> {
    public Telekinesis() {
        super("telekinesis", Boolean.class);
        TelekinesisUtils.registerTest(player -> this.getStrengthForPlayer(player) != null);
    }
}
