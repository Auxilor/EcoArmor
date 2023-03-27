package com.willfp.ecoarmor.sets

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableList
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.config.readConfig
import com.willfp.eco.core.config.updating.ConfigUpdater
import com.willfp.eco.core.registry.Registry
import com.willfp.ecoarmor.EcoArmorPlugin
import com.willfp.ecoarmor.upgrades.Tiers
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory
import com.willfp.libreforge.loader.configs.LegacyLocation
import java.io.File

object ArmorSets : ConfigCategory("set", "sets") {
    /**
     * Registered armor sets.
     */
    private val registry = Registry<ArmorSet>()

    override val legacyLocation = LegacyLocation(
        "ecoarmor",
        "sets",
        emptyList()
    )

    /**
     * Get all registered [ArmorSet]s.
     *
     * @return A list of all [ArmorSet]s.
     */
    @JvmStatic
    fun values(): List<ArmorSet> {
        return ImmutableList.copyOf(registry.values())
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

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        registry.register(ArmorSet(id, config, plugin as EcoArmorPlugin))
    }
}
