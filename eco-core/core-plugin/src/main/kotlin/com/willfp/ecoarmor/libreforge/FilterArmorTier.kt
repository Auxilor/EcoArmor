package com.willfp.ecoarmor.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoarmor.api.event.ArmorTierEvent
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterArmorTier : Filter<NoCompileData, Collection<String>>("armor_tier") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? ArmorTierEvent ?: return true

        return value.any { it.equals(event.tier.id, ignoreCase = true) }
    }
}