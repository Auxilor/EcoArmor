package com.willfp.ecoarmor.upgrades

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableList
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.TransientConfig
import com.willfp.eco.core.config.updating.ConfigUpdater
import com.willfp.ecoarmor.EcoArmorPlugin
import com.willfp.ecoarmor.EcoArmorPlugin.Companion.instance
import java.io.File

object Tiers {
    /**
     * Registered tiers.
     */
    private val BY_ID: BiMap<String?, Tier> = HashBiMap.create()

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
        return BY_ID[name]
    }

    /**
     * Get all registered [Tiers]s.
     *
     * @return A list of all [Tiers]s.
     */
    @JvmStatic
    fun values(): List<Tier> {
        return ImmutableList.copyOf(BY_ID.values)
    }

    /**
     * Add new [Tier] to EcoArmor.
     *
     * @param tier The [Tier] to add.
     */
    @JvmStatic
    fun addNewTier(tier: Tier) {
        BY_ID.remove(tier.id)
        BY_ID[tier.id] = tier
    }

    /**
     * Update.
     *
     * @param plugin Instance of EcoArmor.
     */
    @ConfigUpdater
    @JvmStatic
    fun reload(plugin: EcoArmorPlugin) {
        BY_ID.clear()

        for ((id, config) in plugin.fetchConfigs("tiers")) {
            Tier(id, config, plugin)
        }

        val ecoArmorYml = TransientConfig(File(plugin.dataFolder, "ecoarmor.yml"), ConfigType.YAML)

        for (config in ecoArmorYml.getSubsections("tiers")) {
            Tier(config.getString("id"), config, plugin)
        }

        defaultTier = getByID("default")!!
    }

    init {
        reload(instance)
    }
}