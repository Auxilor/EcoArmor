package com.willfp.ecoarmor.effects.effects;

import com.willfp.eco.util.NumberUtils;
import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class WarpChance extends Effect {
    public WarpChance() {
        super("warp-chance");
    }

    @EventHandler
    public void onDamage(@NotNull final EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        Player player = (Player) event.getDamager();
        LivingEntity victim = (LivingEntity) event.getEntity();

        double chance = ArmorUtils.getEffectStrength(player, this);

        if (NumberUtils.randFloat(0, 100) > chance) {
            return;
        }

        Vector between = victim.getLocation().subtract(player.getLocation()).toVector();
        Location behind = victim.getLocation().add(between);

        behind.setYaw(player.getLocation().getYaw() + 180);

        Block head = behind.add(0, 1.4, 0).getBlock();

        if (!head.getType().isAir()) {
            return;
        }

        player.getLocation().setYaw(player.getLocation().getYaw() + 180);
        player.teleport(behind);
    }
}
