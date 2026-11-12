package com.willfp.ecoarmor.upgrades

import com.google.common.collect.ImmutableList
import wiki.ecohub.config.Config
import wiki.ecohub.config.LegacyLocation
import wiki.ecohub.config.category.NativeConfigCategory
import wiki.ecohub.core.plugin.EcoPlugin
import wiki.ecohub.core.registry.Registry
import wiki.ecohub.modules.recipe.Recipes

object Tiers : ConfigCategory("tier", "tiers") {
    /**
     * Registered tiers.
     */
    private val registry = Registry<Tier>()

    @Volatile
    private var cachedValues: List<Tier> = emptyList()

    override val supportsSharing = false

    override val legacyLocation = LegacyLocation(
        "ecoarmor.yml",
        "tiers"
    )

    /**
     * Default tier.
     */
    @JvmStatic
    @Volatile
    lateinit var defaultTier: Tier

    /**
     * Get [Tiers] matching ID.
     *
     * @param name The name to search for.
     * @return The matching [Tiers], or null if not found.
     */
    @JvmStatic
    fun getByID(name: String?): Tier? {
        if (name == null) {
            return null
        }

        return registry[name]
    }

    /**
     * Get all registered [Tiers]s.
     *
     * @return A list of all [Tiers]s.
     */
    @JvmStatic
    fun values(): List<Tier> {
        return cachedValues
    }

    override fun clear(plugin: LibreforgePlugin) {
        for (tier in registry.values()) {
            Recipes.unregister(plugin.createNamespacedKey("upgrade_crystal_${tier.id.lowercase()}"))
        }
        registry.clear()
        cachedValues = emptyList()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        registry.register(Tier(id, config))
    }

    override fun afterReload(plugin: LibreforgePlugin) {
        cachedValues = ImmutableList.copyOf(registry.values())
        defaultTier = getByID("default")!!

        val legacyTierIds = cachedValues.mapNotNull { tier -> tier.id.takeIf { tier.usesLegacyStats } }.sorted()
        if (legacyTierIds.isNotEmpty()) {
            plugin.logger.warning(
                "Tiers using deprecated stat options: ${legacyTierIds.joinToString()}. " +
                        "They still work but will be removed in a future version; " +
                        "move them to components with attribute_modifiers."
            )
        }
    }
}
