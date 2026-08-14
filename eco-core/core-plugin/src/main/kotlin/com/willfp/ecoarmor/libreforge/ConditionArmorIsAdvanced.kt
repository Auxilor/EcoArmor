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
import com.willfp.libreforge.getProvider
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

object ConditionArmorIsAdvanced : Condition<NoCompileData>("armor_is_advanced") {
    override val description = "Passes when the entity's armor is advanced."

    override val categories = setOf("inventory")

    override val additionalInfo = listOf(
        "If a slot is specified, only the piece in that slot is checked, and the condition "
                + "fails when that slot is empty.",
        "Otherwise, effects on a single armor piece check that piece, and effects on a whole "
                + "set require the full set to be advanced."
    )

    override val arguments = arguments {
        optional(
            "slot",
            description = "If specified, only checks the armor piece worn in this slot.",
            type = ArgType.STRING,
            choices = slotChoices,
            example = "helmet"
        )
        optional(
            "advanced",
            description = "The advanced state to require. Set to false to require armor that is not advanced.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val entity = dispatcher.get<LivingEntity>() ?: return false
        val required = if (config.has("advanced")) config.getBool("advanced") else true

        if (config.has("slot")) {
            val slot = config.getArmorSlot() ?: return false
            val item = ArmorUtils.getItemInSlot(entity, slot) ?: return false
            return ArmorUtils.isAdvanced(item) == required
        }

        // Effects attached to a single armor piece are provided that piece.
        val provided = holder.getProvider<ItemStack>()
        if (provided != null) {
            return ArmorUtils.isAdvanced(provided) == required
        }

        return ArmorUtils.isWearingAdvanced(entity) == required
    }
}
