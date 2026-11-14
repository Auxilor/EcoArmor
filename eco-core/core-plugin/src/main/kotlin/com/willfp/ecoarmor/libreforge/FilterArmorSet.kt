package com.willfp.ecoarmor.libreforge

import com.willfp.ecoarmor.api.event.ArmorAdvanceEvent
import com.willfp.ecoarmor.api.event.ArmorTierEvent
import com.willfp.ecoarmor.sets.ArmorUtils
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.filter.Filter
import wiki.ecohub.rules.trigger.TriggerData

object FilterArmorSet : Filter<NoCompileData, Collection<String>>("armor_set") {
    override val description = "Matches when the armor set involved in the trigger has one of the given set IDs."

    override val categories = setOf("inventory")

    override val valueType = ArgType.STRING_LIST

    override val additionalInfo = listOf(
        "Passes automatically for triggers that are not related to an armor set."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val setId = when (val event = data.event) {
            is ArmorAdvanceEvent -> event.set.id
            is ArmorTierEvent -> ArmorUtils.getSetOnItem(event.item)?.id
            else -> return true
        } ?: return false

        return value.any { it.equals(setId, ignoreCase = true) }
    }
}