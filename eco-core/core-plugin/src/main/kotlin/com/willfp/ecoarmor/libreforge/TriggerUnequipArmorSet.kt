package com.willfp.ecoarmor.libreforge

import com.willfp.ecoarmor.api.event.PlayerArmorSetUnequipEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerUnequipArmorSet : Trigger("unequip_armor_set") {
    override val description = "Fires when the player stops wearing a full armor set."

    override val categories = setOf("inventory")

    override val parameterDescriptions = mapOf(
        TriggerParameter.TEXT to "The ID of the armor set that was unequipped."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.EVENT
    )

    @EventHandler
    fun handle(event: PlayerArmorSetUnequipEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                text = event.set.id,
                event = event
            )
        )
    }
}
