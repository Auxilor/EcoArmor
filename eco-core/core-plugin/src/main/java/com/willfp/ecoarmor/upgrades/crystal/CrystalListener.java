package com.willfp.ecoarmor.upgrades.crystal;

import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.ecoarmor.config.EcoArmorConfigs;
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

        String tier = ArmorUtils.getCrystalTier(cursor);

        if (tier == null) {
            return;
        }

        if (current.getType() == Material.AIR) {
            return;
        }

        String previousTier = ArmorUtils.getTier(current);

        String prereq = EcoArmorConfigs.TIERS.getString(tier + ".requires-tier");
        boolean allowed = false;
        if (prereq.equals("none")) {
            allowed = true;
        } else if (prereq.equals(previousTier)) {
            allowed = true;
        }

        if (!allowed) {
            return;
        }

        ArmorUtils.setTier(current, tier);

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
