package com.willfp.ecoarmor.upgrades.listeners;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import com.willfp.ecoarmor.upgrades.Tier;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CrystalListener extends PluginDependent implements Listener {
    /**
     * Create new listeners for dragging crystals onto items.
     *
     * @param plugin The plugin to listen for.
     */
    public CrystalListener(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    /**
     * Listen for inventory click event.
     *
     * @param event The event to handle.
     */
    @EventHandler
    public void onDrag(@NotNull final InventoryClickEvent event) {
        if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        if (current == null || cursor == null) {
            return;
        }

        Tier crystalTier = ArmorUtils.getCrystalTier(cursor);

        if (crystalTier == null) {
            return;
        }

        if (ArmorUtils.getSetOnItem(current) == null) {
            return;
        }

        if (current.getType() == Material.AIR) {
            return;
        }

        Tier previousTier = ArmorUtils.getTier(current);
        boolean allowed = false;
        List<Tier> prereq = crystalTier.getRequiredTiersForApplication();

        if (prereq.isEmpty() || prereq.contains(previousTier)) {
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

        if (ArmorUtils.getCrystalTier(item) != null) {
            event.setCancelled(true);
        }
    }
}
