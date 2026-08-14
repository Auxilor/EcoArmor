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
import com.willfp.libreforge.getStrings
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

object ConditionHasArmorTier : Condition<NoCompileData>("has_armor_tier") {
    override val description = "Passes when the entity's armor is on one of the given tiers."

    override val categories = setOf("inventory")

    override val additionalInfo = listOf(
        "If a slot is specified, only the piece in that slot is checked.",
        "Otherwise, effects on a single armor piece check that piece, and effects on a whole "
                + "set require every worn piece of the set to be on one of the tiers."
    )

    override val arguments = arguments {
        require(
            listOf("tiers", "tier"),
            "You must specify the tier(s)!",
            description = "The IDs of the tiers the armor may be on.",
            type = ArgType.STRING_LIST,
            example = listOf("mythic", "ancient")
        )
        optional(
            "slot",
            description = "If specified, only checks the armor piece worn in this slot.",
            type = ArgType.STRING,
            choices = slotChoices,
            example = "helmet"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val entity = dispatcher.get<LivingEntity>() ?: return false
        val tiers = config.getStrings("tiers", "tier")

        if (config.has("slot")) {
            val slot = config.getArmorSlot() ?: return false
            val item = ArmorUtils.getItemInSlot(entity, slot) ?: return false
            return item.isOnTier(tiers)
        }

        // Effects attached to a single armor piece are provided that piece.
        val provided = holder.getProvider<ItemStack>()
        if (provided != null) {
            return provided.isOnTier(tiers)
        }

        val equipment = entity.equipment?.armorContents?.toList() ?: return false
        val set = ArmorUtils.getSetOn(equipment) ?: return false
        val pieces = equipment.filterNotNull().filter { ArmorUtils.getSetOnItem(it) == set }

        return pieces.isNotEmpty() && pieces.all { it.isOnTier(tiers) }
    }
}
