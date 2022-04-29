package com.willfp.ecoarmor.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoarmor.sets.ArmorSets
import com.willfp.ecoarmor.sets.ArmorUtils
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionIsWearingSet: Condition(
    "is_wearing_set"
) {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        val set = ArmorSets.getByID(config.getString("set"))
        if (set != null) {
            return ArmorUtils.getSetOnPlayer(player) == set
        }
        return true
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()
        if (!config.has("set")) {
            violations += ConfigViolation("set", "You must specify an armor set!")
        }
        return violations
    }
}