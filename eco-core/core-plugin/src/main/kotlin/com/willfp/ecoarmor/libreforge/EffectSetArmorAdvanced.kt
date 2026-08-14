package com.willfp.ecoarmor.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoarmor.sets.ArmorUtils
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSetArmorAdvanced : Effect<NoCompileData>("set_armor_advanced") {
    override val description = "Advances an armor piece, or reverts it to its regular form."

    override val categories = setOf("inventory")

    override val isPermanent = false

    override val parameters = setOf(
        TriggerParameter.ITEM
    )

    override val additionalInfo = listOf(
        "Does not fire the armor advance event, so effects using the advance_armor trigger "
                + "will not run.",
        "If no slot is specified, the triggering item is advanced."
    )

    override val arguments = arguments {
        optional(
            "slot",
            description = "If specified, advances the armor piece worn in this slot instead.",
            type = ArgType.STRING,
            choices = slotChoices,
            example = "helmet"
        )
        optional(
            "advanced",
            description = "The advanced state to set. Set to false to revert the piece to its regular form.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val advanced = if (config.has("advanced")) config.getBool("advanced") else true

        if (config.has("slot")) {
            val slot = config.getArmorSlot() ?: return false
            val player = data.player ?: return false
            val item = ArmorUtils.getItemInSlot(player, slot) ?: return false

            if (ArmorUtils.getSetOnItem(item) == null) {
                return false
            }

            ArmorUtils.setAdvanced(item, advanced)
            player.equipment.setItem(slot.slot, item)
            return true
        }

        val item = data.foundItem ?: return false

        if (ArmorUtils.getSetOnItem(item) == null) {
            return false
        }

        ArmorUtils.setAdvanced(item, advanced)
        return true
    }
}
