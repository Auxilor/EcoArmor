package com.willfp.ecoarmor.sets.util;

import com.willfp.eco.util.events.armorequip.ArmorEquipEvent;
import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.ecoarmor.sets.ArmorSet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

public class PotionEffectListener extends PluginDependent implements Listener {
    /**
     * Create new potion effect listener for set effects.
     *
     * @param plugin EcoArmor.
     */
    public PotionEffectListener(@NotNull final AbstractEcoPlugin plugin) {
        super(plugin);
    }

    /**
     * Apply set potion effects.
     *
     * @param event The event to listen for.
     */
    @EventHandler
    public void onEquip(@NotNull final ArmorEquipEvent event) {
        final Player player = event.getPlayer();

        this.getPlugin().getScheduler().runLater(() -> {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getDuration() >= 500000000) {
                    player.removePotionEffect(effect.getType());
                }
            }

            ArmorSet set = ArmorUtils.getSetOnPlayer(player);
            if (set == null) {
                return;
            }

            set.getPotionEffects().forEach((potionEffectType, integer) -> {
                player.addPotionEffect(new PotionEffect(potionEffectType, 0x6fffffff, integer - 1, false, false, true));
            });

            if (ArmorUtils.isAdvanced(player)) {
                set.getAdvancedPotionEffects().forEach((potionEffectType, integer) -> {
                    player.addPotionEffect(new PotionEffect(potionEffectType, 0x6fffffff, integer - 1, false, false, true));
                });
            }
        }, 1);
    }
}
