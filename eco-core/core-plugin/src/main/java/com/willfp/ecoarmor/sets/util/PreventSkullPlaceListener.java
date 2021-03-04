package com.willfp.ecoarmor.sets.util;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

public class PreventSkullPlaceListener implements Listener {
    /**
     * Prevents placing skulls.
     *
     * @param event The event to listen for.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(@NotNull final BlockPlaceEvent event) {
        if (ArmorUtils.getSetOnItem(event.getItemInHand()) != null) {
            event.setCancelled(true);
        }
    }
}
