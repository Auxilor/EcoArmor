package com.willfp.ecoarmor.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoarmor.api.event.ArmorTierEvent
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterArmorTier : Filter<NoCompileData, Collection<String>>("armor_tier") {
    override val description = "Matches when the armor tier reached in the trigger has one of the given tier IDs."

    override val categories = setOf("inventory")

    override val valueType = ArgType.STRING_LIST

    override val additionalInfo = listOf(
        "Passes automatically for triggers that are not related to an armor tier upgrade."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? ArmorTierEvent ?: return true

        return value.any { it.equals(event.tier.id, ignoreCase = true) }
    }
}