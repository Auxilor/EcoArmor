package com.willfp.ecoarmor.upgrades.tier;

import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
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

        Tier crystalTier = ArmorUtils.getCrystalTier(cursor);

        if (crystalTier == null) {
            return;
        }

        if (current.getType() == Material.AIR) {
            return;
        }

        Tier previousTier = ArmorUtils.getTier(current);
        boolean allowed = false;
        Tier prereq = crystalTier.getRequiredTierForApplication();

        if (prereq == null) {
            allowed = true;
        } else if (prereq.equals(previousTier)) {
            allowed = true;
        }

        if (!allowed) {
            return;
        }

        ArmorUtils.setTier(current, crystalTier);

        if (cursor.getAmount() > 1) {
            cursor.setAmount(cursor.getAmount() - 1);
            event.getWhoClicked().setItemOnCursor(cursor);
        } else {
            event.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));
        }

        event.setCancelled(true);
    }

    /**
     * Prevents placing upgrade crystals.
     *
     * @param event The event to listen for.
     */
    @EventHandler
    public void onPlaceCrystal(@NotNull final BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item.getType() != Material.END_CRYSTAL) {
            return;
        }

        if (ArmorUtils.getCrystalTier(item) != null) {
            event.setCancelled(true);
        }
    }
}
