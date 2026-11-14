package com.willfp.ecoarmor.libreforge

import com.willfp.ecoarmor.sets.ArmorUtils
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.config.arguments
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.mutator.Mutator
import wiki.ecohub.rules.mutator.parameterTransformers
import wiki.ecohub.rules.trigger.TriggerData
import wiki.ecohub.rules.trigger.TriggerParameter

object MutatorArmorPieceToItem : Mutator<NoCompileData>("armor_piece_to_item") {
    override val description = "Sets the item to the armor piece worn in a given slot."

    override val categories = setOf("inventory")

    override val additionalInfo = listOf(
        "Sets the item to nothing if the slot is empty, which stops item effects from running."
    )

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.PLAYER becomes TriggerParameter.ITEM
    }

    override val arguments = arguments {
        require(
            "slot",
            "You must specify the slot!",
            description = "The slot to take the armor piece from.",
            type = ArgType.STRING,
            choices = slotChoices,
            example = "helmet"
        )
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val slot = config.getArmorSlot() ?: return data
        val player = data.player ?: return data

        return data.copy(
            item = ArmorUtils.getItemInSlot(player, slot)
        )
    }
}
