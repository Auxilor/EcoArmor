package com.willfp.ecoarmor.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoarmor.sets.ArmorUtils
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.LivingEntity

object ConditionIsWearingSet : Condition<NoCompileData>("is_wearing_set") {
    override val description = "Passes when the entity is wearing the full armor set with the given ID."

    override val categories = setOf("inventory")

    override val arguments = arguments {
        require(
            "set",
            "You must specify the set name!",
            description = "The ID of the armor set the entity must be wearing.",
            type = ArgType.STRING
        )
        optional(
            "advanced",
            description = "If specified, also requires the worn set to be in its advanced (or non-advanced) form to match this value.",
            type = ArgType.BOOLEAN
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val entity = dispatcher.get<LivingEntity>() ?: return false

        if (ArmorUtils.getSetOnEntity(entity)?.id != config.getString("set")) {
            return false
        }

        if (config.has("advanced") && ArmorUtils.isWearingAdvanced(entity) != config.getBool("advanced")) {
            return false
        }

        return true
    }
}
