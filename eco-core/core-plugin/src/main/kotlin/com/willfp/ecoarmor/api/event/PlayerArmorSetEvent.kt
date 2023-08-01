package com.willfp.ecoarmor.api.event

import com.willfp.ecoarmor.sets.ArmorSet
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEvent

abstract class PlayerArmorSetEvent(who: Player, override val set: ArmorSet,
                                   override val advanced: Boolean): PlayerEvent(who), ArmorSetEvent