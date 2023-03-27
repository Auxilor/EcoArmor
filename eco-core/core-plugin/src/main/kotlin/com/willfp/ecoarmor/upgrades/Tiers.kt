package com.willfp.ecoarmor.upgrades

import com.google.common.collect.ImmutableList
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.ecoarmor.EcoArmorPlugin
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory
import com.willfp.libreforge.loader.configs.LegacyLocation

object Tiers : ConfigCategory("tier", "tiers") {
    /**
     * Registered tiers.
     */
    private val registry = Registry<Tier>()

    override val supportsSharing = false

    override val legacyLocation = LegacyLocation(
        "ecoarmor.yml",
        "tiers"
    )

    /**
     * Default tier.
     */
    @JvmStatic
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
        return ImmutableList.copyOf(registry.values())
    }

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        registry.register(Tier(id, config, plugin as EcoArmorPlugin))
    }

    override fun afterReload(plugin: LibreforgePlugin) {
        defaultTier = getByID("default")!!
    }
}
