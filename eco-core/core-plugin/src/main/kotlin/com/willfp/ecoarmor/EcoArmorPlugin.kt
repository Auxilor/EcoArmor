package com.willfp.ecoarmor

import com.willfp.ecoarmor.commands.ecoArmorCommand
import com.willfp.ecoarmor.display.ArmorDisplay
import com.willfp.ecoarmor.libreforge.ConditionArmorIsAdvanced
import com.willfp.ecoarmor.libreforge.ConditionHasArmorTier
import com.willfp.ecoarmor.libreforge.ConditionIsWearingSet
import com.willfp.ecoarmor.libreforge.EffectCountTowardSet
import com.willfp.ecoarmor.libreforge.EffectSetArmorAdvanced
import com.willfp.ecoarmor.libreforge.EffectSetArmorTier
import com.willfp.ecoarmor.libreforge.FilterArmorSet
import com.willfp.ecoarmor.libreforge.FilterArmorTier
import com.willfp.ecoarmor.libreforge.MutatorArmorPieceToItem
import com.willfp.ecoarmor.libreforge.TriggerAdvanceArmor
import com.willfp.ecoarmor.libreforge.TriggerEquipArmorSet
import com.willfp.ecoarmor.libreforge.TriggerUnequipArmorSet
import com.willfp.ecoarmor.libreforge.TriggerUpgradeArmorTier
import com.willfp.ecoarmor.sets.ArmorSetEquipSoundListeners
import com.willfp.ecoarmor.sets.ArmorSets
import com.willfp.ecoarmor.sets.ArmorUtils
import com.willfp.ecoarmor.sets.EffectiveDurabilityListener
import com.willfp.ecoarmor.sets.PlayerArmorSetEventListeners
import com.willfp.ecoarmor.sets.PreventSkullPlaceListener
import com.willfp.ecoarmor.upgrades.AdvancementShardListener
import com.willfp.ecoarmor.upgrades.CrystalListener
import com.willfp.ecoarmor.upgrades.TierArgParser
import com.willfp.ecoarmor.upgrades.Tiers
import com.willfp.ecoarmor.util.DiscoverRecipeListener
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Listener
import wiki.ecohub.config.category.NativeConfigCategory
import wiki.ecohub.core.command.EcoCommand
import wiki.ecohub.core.plugin.EcoPlugin
import wiki.ecohub.game.item.Items
import wiki.ecohub.modules.menus.drag.DragAndDropHandlers
import wiki.ecohub.modules.menus.drag.DragAndDropSettings
import wiki.ecohub.rules.condition.Conditions
import wiki.ecohub.rules.effect.Effects
import wiki.ecohub.rules.filter.Filters
import wiki.ecohub.rules.holder.registerSpecificHolderProvider
import wiki.ecohub.rules.mutator.Mutators
import wiki.ecohub.rules.trigger.Triggers
import wiki.ecohub.ui.display.DisplayModule

internal lateinit var plugin: EcoArmorPlugin
    private set

class EcoArmorPlugin : EcoPlugin() {
    init {
        plugin = this
        Items.registerArgParser(TierArgParser())
    }

    override fun handleLoad() {
        Conditions.register(ConditionIsWearingSet)
        Conditions.register(ConditionHasArmorTier)
        Conditions.register(ConditionArmorIsAdvanced)
        Triggers.register(TriggerAdvanceArmor)
        Triggers.register(TriggerUpgradeArmorTier)
        Triggers.register(TriggerEquipArmorSet)
        Triggers.register(TriggerUnequipArmorSet)
        Effects.register(EffectSetArmorTier)
        Effects.register(EffectSetArmorAdvanced)
        Effects.register(EffectCountTowardSet)
        Mutators.register(MutatorArmorPieceToItem)
        Filters.register(FilterArmorSet)
        Filters.register(FilterArmorTier)
    }

    override fun handleEnable() {
        registerSpecificHolderProvider<LivingEntity> {
            ArmorUtils.getActiveHolders(it)
        }

        DragAndDropHandlers.register(
            CrystalListener,
            DragAndDropSettings(allowCraftingTableNonResultSlot = true)
        )
        DragAndDropHandlers.register(
            AdvancementShardListener,
            DragAndDropSettings(allowCraftingTableNonResultSlot = true)
        )

        // Recipes are registered on a 1-tick delay, so rebuild cache after they exist.
        scheduler.global().runLater(2) {
            DiscoverRecipeListener.reloadRecipeCache()
        }
    }

    override fun handleReload() {
        ArmorUtils.clearCache()
        scheduler.global().runLater(2) {
            DiscoverRecipeListener.reloadRecipeCache()
        }
    }

    override fun handleDisable() {
        DragAndDropHandlers.unregisterAll("ecoarmor")
    }

    override fun loadConfigCategories(): List<NativeConfigCategory> {
        return listOf(
            Tiers,
            ArmorSets
        )
    }

    override fun loadPluginCommands(): List<EcoCommand> {
        return listOf(
            ecoArmorCommand()
        )
    }

    override fun loadListeners(): List<Listener> {
        return listOf(
            CrystalListener,
            EffectiveDurabilityListener,
            DiscoverRecipeListener,
            PreventSkullPlaceListener,
            ArmorSetEquipSoundListeners,
            PlayerArmorSetEventListeners
        )
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun createDisplayModule(): DisplayModule {
        return ArmorDisplay
    }
}
