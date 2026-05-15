package com.willfp.ecoarmor.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoarmor.api.event.ArmorAdvanceEvent
import com.willfp.ecoarmor.api.event.ArmorTierEvent
import com.willfp.ecoarmor.sets.ArmorUtils
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterArmorSet : Filter<NoCompileData, Collection<String>>("armor_set") {
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