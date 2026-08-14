package com.willfp.ecoarmor.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoarmor.sets.ArmorUtils
import com.willfp.ecoarmor.upgrades.Tiers
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSetArmorTier : Effect<NoCompileData>("set_armor_tier") {
    override val description = "Sets the tier of an armor piece."

    override val categories = setOf("inventory")

    override val isPermanent = false

    override val parameters = setOf(
        TriggerParameter.ITEM
    )

    override val additionalInfo = listOf(
        "Does not fire the armor tier upgrade event, so effects using the upgrade_armor_tier "
                + "trigger will not run.",
        "If no slot is specified, the tier is set on the triggering item."
    )

    override val arguments = arguments {
        require(
            "tier",
            "You must specify the tier!",
            description = "The ID of the tier to set.",
            type = ArgType.STRING,
            example = "mythic"
        )
        optional(
            "slot",
            description = "If specified, sets the tier on the armor piece worn in this slot instead.",
            type = ArgType.STRING,
            choices = slotChoices,
            example = "helmet"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val tier = Tiers.getByID(config.getString("tier")) ?: return false

        if (config.has("slot")) {
            val slot = config.getArmorSlot() ?: return false
            val player = data.player ?: return false
            val item = ArmorUtils.getItemInSlot(player, slot) ?: return false

            if (ArmorUtils.getSetOnItem(item) == null) {
                return false
            }

            ArmorUtils.setTier(item, tier)
            player.equipment.setItem(slot.slot, item)
            return true
        }

        val item = data.foundItem ?: return false

        if (ArmorUtils.getSetOnItem(item) == null) {
            return false
        }

        ArmorUtils.setTier(item, tier)
        return true
    }
}
