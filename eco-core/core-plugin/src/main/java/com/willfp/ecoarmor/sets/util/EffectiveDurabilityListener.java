package com.willfp.ecoarmor.sets.util;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.util.NumberUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class EffectiveDurabilityListener extends PluginDependent<EcoPlugin> implements Listener {
    /**
     * Create new effective durability listeners.
     *
     * @param plugin The plugin.
     */
    public EffectiveDurabilityListener(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    /**
     * Make durability act as effective.
     *
     * @param event The event to listen for.
     */
    @EventHandler
    public void listener(@NotNull final PlayerItemDamageEvent event) {
        ItemStack itemStack = event.getItem();
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) {
            return;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();

        Integer effectiveDurability = container.get(this.getPlugin().getNamespacedKeyFactory().create("effective-durability"), PersistentDataType.INTEGER);

        if (effectiveDurability == null) {
            return;
        }

        int maxDurability = itemStack.getType().getMaxDurability();

        double ratio = (double) effectiveDurability / maxDurability;

        double chance = 1 / ratio;

        if (NumberUtils.randFloat(0, 1) > chance) {
            event.setCancelled(true);
        }
    }
}
