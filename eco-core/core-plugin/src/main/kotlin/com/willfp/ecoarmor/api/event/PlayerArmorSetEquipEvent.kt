package com.willfp.ecoarmor.api.event

import com.willfp.ecoarmor.sets.ArmorSet
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

class PlayerArmorSetEquipEvent(
    who: Player,
    override val set: ArmorSet,
    override val advanced: Boolean
) : PlayerArmorSetEvent(who, set, advanced) {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }
}