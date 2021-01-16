package com.willfp.ecoarmor.tiers;

import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CrystalListener extends PluginDependent implements Listener {
    /**
     * Create new listeners for dragging crystals onto items.
     *
     * @param plugin The plugin to listen for.
     */
    public CrystalListener(@NotNull final AbstractEcoPlugin plugin) {
        super(plugin);
    }

    /**
     * Listen for inventory click event.
     *
     * @param event The event to handle.
     */
    @EventHandler
    public void onDrag(@NotNull final InventoryClickEvent event) {
        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        if (current == null || cursor == null) {
            return;
        }

        if (cursor.getType() != Material.END_CRYSTAL) {
            return;
        }

        String tier = ArmorUtils.getCrystalTier(cursor);

        if (tier == null) {
            return;
        }

        if (current.getType() == Material.AIR) {
            return;
        }

        ArmorUtils.setTier(current, tier);

        event.getWhoClicked().setItemOnCursor(null);

        event.setCancelled(true);
    }
}