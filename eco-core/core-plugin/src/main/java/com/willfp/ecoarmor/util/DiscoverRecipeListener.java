package com.willfp.ecoarmor.util;

import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

public class DiscoverRecipeListener extends PluginDependent implements Listener {
    /**
     * Register listener.
     *
     * @param plugin Talismans.
     */
    public DiscoverRecipeListener(@NotNull final AbstractEcoPlugin plugin) {
        super(plugin);
    }

    /**
     * Unlock all recipes on player join.
     *
     * @param event The event to listen for.
     */
    @EventHandler
    public void onJoin(@NotNull final PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (this.getPlugin().getConfigYml().getBool("discover-recipes")) {
            Bukkit.getServer().recipeIterator().forEachRemaining(recipe -> {
                if (recipe instanceof ShapedRecipe) {
                    NamespacedKey key = ((ShapedRecipe) recipe).getKey();
                    if (key.getNamespace().equals("ecoarmor")) {
                        if (!key.getKey().contains("displayed")) {
                            player.discoverRecipe(key);
                        }
                    }
                }
            });
        }
    }
}
