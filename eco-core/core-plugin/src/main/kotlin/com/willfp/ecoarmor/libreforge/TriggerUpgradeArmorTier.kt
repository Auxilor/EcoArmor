package com.willfp.ecoarmor.libreforge

import com.willfp.ecoarmor.api.event.ArmorTierEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerUpgradeArmorTier : Trigger("upgrade_armor_tier") {
    override val description = "Fires when the player upgrades the tier of an armor piece."

    override val categories = setOf("inventory")

    override val parameterDescriptions = mapOf(
        TriggerParameter.ITEM to "The armor piece whose tier was upgraded.",
        TriggerParameter.TEXT to "The ID of the tier that was reached."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.ITEM,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: ArmorTierEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                item = event.item,
                location = player.location,
                text = event.tier.id,
                event = event
            )
        )
    }
}