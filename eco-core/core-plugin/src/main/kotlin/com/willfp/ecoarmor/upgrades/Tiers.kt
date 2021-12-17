package com.willfp.ecoarmor.upgrades

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableList
import com.willfp.eco.core.config.updating.ConfigUpdater
import com.willfp.ecoarmor.EcoArmorPlugin
import com.willfp.ecoarmor.EcoArmorPlugin.Companion.instance

object Tiers {
    /**
     * Registered tiers.
     */
    private val BY_ID: BiMap<String?, Tier> = HashBiMap.create()

    /**
     * Default tier.
     */
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
        for (tierConfig in plugin.ecoArmorYml.getSubsections("tiers")) {
            Tier(tierConfig, plugin)
        }
        defaultTier = getByID("default")!!
    }

    init {
        reload(instance)
    }
}