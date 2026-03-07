package com.willfp.ecoarmor.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoarmor.sets.ArmorUtils
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.LivingEntity

object ConditionIsWearingSet : Condition<NoCompileData>("is_wearing_set") {
    override val arguments = arguments {
        require("set", "You must specify the set name!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val entity = dispatcher.get<LivingEntity>() ?: return false

        return ArmorUtils.getSetOnEntity(entity)?.id == config.getString("set")
    }
}
