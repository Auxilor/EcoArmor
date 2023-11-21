package com.willfp.ecoarmor

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.items.Items
import com.willfp.ecoarmor.sets.PlayerArmorSetEventListeners
import com.willfp.ecoarmor.commands.CommandEcoArmor
import com.willfp.ecoarmor.display.ArmorDisplay
import com.willfp.ecoarmor.libreforge.ConditionIsWearingSet
import com.willfp.ecoarmor.sets.ArmorSetEquipSoundListeners
import com.willfp.ecoarmor.sets.ArmorSets
import com.willfp.ecoarmor.sets.ArmorUtils
import com.willfp.ecoarmor.sets.EffectiveDurabilityListener
import com.willfp.ecoarmor.sets.PreventSkullPlaceListener
import com.willfp.ecoarmor.upgrades.AdvancementShardListener
import com.willfp.ecoarmor.upgrades.CrystalListener
import com.willfp.ecoarmor.upgrades.TierArgParser
import com.willfp.ecoarmor.upgrades.Tiers
import com.willfp.ecoarmor.util.DiscoverRecipeListener
import com.willfp.libreforge.SimpleProvidedHolder
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory
import com.willfp.libreforge.registerHolderProvider
import com.willfp.libreforge.registerSpecificHolderProvider
import org.bukkit.entity.Player
import org.bukkit.event.Listener

class EcoArmorPlugin : LibreforgePlugin() {
    init {
        instance = this
        Items.registerArgParser(TierArgParser())
    }

    override fun handleLoad() {
        Conditions.register(ConditionIsWearingSet)
    }

    override fun handleEnable() {
        registerSpecificHolderProvider<Player> {
            ArmorUtils.getActiveHolders(it)
        }
    }

    override fun loadConfigCategories(): List<ConfigCategory> {
        return listOf(
            Tiers,
            ArmorSets
        )
    }

    override fun loadPluginCommands(): List<PluginCommand> {
        return listOf(
            CommandEcoArmor(this)
        )
    }

    override fun loadListeners(): List<Listener> {
        return listOf(
            CrystalListener(),
            AdvancementShardListener(this),
            EffectiveDurabilityListener(this),
            DiscoverRecipeListener(this),
            PreventSkullPlaceListener(),
            PlayerArmorSetEventListeners(),
            ArmorSetEquipSoundListeners()
        )
    }

    override fun createDisplayModule(): DisplayModule {
        return ArmorDisplay(this)
    }

    companion object {
        @JvmStatic
        lateinit var instance: EcoArmorPlugin
    }
}
