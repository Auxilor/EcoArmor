package com.willfp.ecoarmor.libreforge

import com.willfp.ecoarmor.sets.ArmorSets
import com.willfp.ecoarmor.sets.ArmorUtils
import org.bukkit.entity.LivingEntity
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.config.arguments
import wiki.ecohub.config.section.getStrings
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.condition.Condition
import wiki.ecohub.rules.holder.Dispatcher
import wiki.ecohub.rules.holder.ProvidedHolder
import wiki.ecohub.rules.holder.get

object ConditionIsWearingSet : Condition<NoCompileData>("is_wearing_set") {
    override val description = "Passes when the entity is wearing the armor set with the given ID."

    override val categories = setOf("inventory")

    override val additionalInfo = listOf(
        "Requires the full set unless an amount is specified.",
        "Pieces given by the count_toward_set effect count towards the set and the amount."
    )

    override val arguments = arguments {
        require(
            "set",
            "You must specify the set name!",
            description = "The ID of the armor set the entity must be wearing.",
            type = ArgType.STRING
        )
        optional(
            "amount",
            description = "If specified, requires this many pieces of the set to be worn "
                    + "rather than the full set.",
            type = ArgType.INT,
            example = "2"
        )
        optional(
            "advanced",
            description = "If specified, also requires the worn set to be in its advanced (or non-advanced) form to match this value.",
            type = ArgType.BOOLEAN
        )
        optional(
            listOf("tiers", "tier"),
            description = "If specified, also requires every worn piece of the set to be on one of these tiers.",
            type = ArgType.STRING_LIST,
            example = listOf("mythic", "ancient")
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val entity = dispatcher.get<LivingEntity>() ?: return false
        val setId = config.getString("set")

        val equipment = entity.equipment?.armorContents?.toList() ?: return false
        val pieces = equipment.filterNotNull().filter { ArmorUtils.getSetOnItem(it)?.id == setId }

        val partial = config.has("amount")

        if (partial) {
            val set = ArmorSets.getByID(setId) ?: return false

            if ((ArmorUtils.getSetCountsOnEntity(entity)[set] ?: 0) < config.getInt("amount")) {
                return false
            }
        } else {
            if (ArmorUtils.getSetOnEntity(entity)?.id != setId) {
                return false
            }
        }

        if (config.has("advanced")) {
            val advanced = if (partial) {
                pieces.isNotEmpty() && pieces.all { ArmorUtils.isAdvanced(it) }
            } else {
                ArmorUtils.isWearingAdvanced(entity)
            }

            if (advanced != config.getBool("advanced")) {
                return false
            }
        }

        if (config.has("tier") || config.has("tiers")) {
            val tiers = config.getStrings("tiers", "tier")

            if (pieces.isEmpty() || !pieces.all { it.isOnTier(tiers) }) {
                return false
            }
        }

        return true
    }
}
