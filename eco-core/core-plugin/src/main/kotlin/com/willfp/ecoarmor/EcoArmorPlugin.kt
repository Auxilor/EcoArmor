package com.willfp.ecoarmor

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.integrations.IntegrationLoader
import com.willfp.ecoarmor.commands.CommandEcoarmor
import com.willfp.ecoarmor.config.EcoArmorYml
import com.willfp.ecoarmor.display.ArmorDisplay
import com.willfp.ecoarmor.sets.ArmorSets
import com.willfp.ecoarmor.sets.ArmorUtils
import com.willfp.ecoarmor.sets.EffectiveDurabilityListener
import com.willfp.ecoarmor.sets.PreventSkullPlaceListener
import com.willfp.ecoarmor.upgrades.AdvancementShardListener
import com.willfp.ecoarmor.upgrades.CrystalListener
import com.willfp.ecoarmor.upgrades.Tiers
import com.willfp.ecoarmor.util.DiscoverRecipeListener
import com.willfp.ecoarmor.util.EffectListener
import com.willfp.libreforge.LibReforge.disable
import com.willfp.libreforge.LibReforge.enable
import com.willfp.libreforge.LibReforge.getIntegrationLoaders
import com.willfp.libreforge.LibReforge.init
import com.willfp.libreforge.LibReforge.registerHolderProvider
import com.willfp.libreforge.LibReforge.reload
import org.bukkit.event.Listener

class EcoArmorPlugin : EcoPlugin(687, 10002, "&c") {
    val ecoArmorYml: EcoArmorYml

    init {
        instance = this
        ecoArmorYml = EcoArmorYml(this)
        init(this)
        registerHolderProvider { player ->
            val active = ArmorUtils.getActiveSet(player)
            if (active == null) {
                emptyList()
            } else {
                listOf(active)
            }
        }
    }

    override fun handleEnable() {
        enable(this)
    }

    override fun handleDisable() {
        disable(this)
    }

    override fun handleReload() {
        logger.info(Tiers.values().size.toString() + " Tiers Loaded")
        logger.info(ArmorSets.values().size.toString() + " Sets Loaded")
        reload(this)
    }

    override fun loadPluginCommands(): List<PluginCommand> {
        return listOf(
            CommandEcoarmor(this)
        )
    }

    override fun loadListeners(): List<Listener> {
        return listOf(
            CrystalListener(this),
            AdvancementShardListener(this),
            EffectiveDurabilityListener(this),
            DiscoverRecipeListener(this),
            PreventSkullPlaceListener(),
            EffectListener()
        )
    }

    override fun loadIntegrationLoaders(): List<IntegrationLoader> {
        return getIntegrationLoaders()
    }

    override fun createDisplayModule(): DisplayModule {
        return ArmorDisplay(this)
    }

    override fun getMinimumEcoVersion(): String {
        return "6.17.0"
    }

    companion object {
        @JvmStatic
        lateinit var instance: EcoArmorPlugin
    }
}