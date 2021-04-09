package com.willfp.ecoarmor.upgrades.listeners;


import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.ArmorSets;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class AdvancementShardListener extends PluginDependent implements Listener {
    /**
     * Create new listeners for dragging crystals onto items.
     *
     * @param plugin The plugin to listen for.
     */
    public AdvancementShardListener(@NotNull final EcoPlugin plugin) {
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

        ItemMeta cursorMeta = cursor.getItemMeta();

        if (cursorMeta == null) {
            return;
        }

        String shardSet = cursorMeta.getPersistentDataContainer().get(this.getPlugin().getNamespacedKeyFactory().create("advancement-shard"), PersistentDataType.STRING);

        if (shardSet == null) {
            return;
        }

        ArmorSet set = ArmorUtils.getSetOnItem(current);

        if (set == null) {
            return;
        }

        if (!ArmorSets.getByName(shardSet).getName().equals(set.getName())) {
            return;
        }

        if (current.getType() == Material.AIR) {
            return;
        }

        if (ArmorUtils.isAdvanced(current)) {
            return;
        }

        ArmorUtils.setAdvanced(current, true);

        if (cursor.getAmount() > 1) {
            cursor.setAmount(cursor.getAmount() - 1);
            event.getWhoClicked().setItemOnCursor(cursor);
        } else {
            event.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));
        }
        event.setCancelled(true);
    }
}
