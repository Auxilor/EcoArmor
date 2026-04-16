package com.willfp.ecoarmor.util

import com.willfp.ecoarmor.plugin
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object DiscoverRecipeListener : Listener {
    private var cachedRecipeKeys = emptyList<NamespacedKey>()

    fun reloadRecipeCache() {
        val namespace = plugin.name.lowercase()
        cachedRecipeKeys = buildList {
            Bukkit.getServer().recipeIterator().forEachRemaining { recipe ->
                if (recipe is Keyed) {
                    val key = recipe.key
                    if (key.namespace == namespace && !key.key.contains("displayed")) {
                        add(key)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (!plugin.configYml.getBool("discover-recipes")) {
            return
        }
        for (key in cachedRecipeKeys) {
            event.player.discoverRecipe(key)
        }
    }
}