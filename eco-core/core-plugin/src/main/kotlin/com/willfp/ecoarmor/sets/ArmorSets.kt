package com.willfp.ecoarmor.sets

import com.google.common.collect.ImmutableList
import wiki.ecohub.config.Config
import wiki.ecohub.config.LegacyLocation
import wiki.ecohub.config.category.NativeConfigCategory
import wiki.ecohub.core.plugin.EcoPlugin
import wiki.ecohub.core.registry.Registry
import wiki.ecohub.modules.recipe.Recipes

object ArmorSets : NativeConfigCategory("sets", "set") {
    /**
     * Registered armor sets.
     */
    private val registry = Registry<ArmorSet>()

    @Volatile
    private var cachedValues: List<ArmorSet> = emptyList()

    override val legacyLocation = LegacyLocation(
        "ecoarmor.yml",
        "sets"
    )

    /**
     * Get all registered [ArmorSet]s.
     *
     * @return A list of all [ArmorSet]s.
     */
    @JvmStatic
    fun values(): List<ArmorSet> {
        return cachedValues
    }

    /**
     * Get [ArmorSet] matching ID.
     *
     * @param name The name to search for.
     * @return The matching [ArmorSet], or null if not found.
     */
    @JvmStatic
    fun getByID(name: String): ArmorSet? {
        return registry[name]
    }

    override fun clear(plugin: EcoPlugin) {
        for (set in registry.values()) {
            Recipes.unregister(plugin.createNamespacedKey("${set.id}_shard"))
            for (slot in ArmorSlot.entries) {
                Recipes.unregister(plugin.createNamespacedKey("${set.id}_${slot.name.lowercase()}"))
            }
        }
        registry.clear()
        cachedValues = emptyList()
    }

    override fun acceptConfig(plugin: EcoPlugin, id: String, config: Config) {
        registry.register(ArmorSet(id, config))
    }

    override fun afterReload(plugin: EcoPlugin) {
        cachedValues = ImmutableList.copyOf(registry.values())
    }
}
