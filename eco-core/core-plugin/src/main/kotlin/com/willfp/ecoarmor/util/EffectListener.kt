package com.willfp.ecoarmor.util

import com.willfp.eco.core.events.ArmorChangeEvent
import com.willfp.ecoarmor.sets.ArmorSet
import com.willfp.ecoarmor.sets.ArmorUtils
import com.willfp.libreforge.updateEffects
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import java.lang.IllegalArgumentException
import java.lang.NullPointerException

class EffectListener: Listener {
    @EventHandler
    fun armorEquipListener(event: ArmorChangeEvent) {
        val player = event.player

        player.updateEffects()

        val active = getSetOnItems(event.after) ?: return
        val prevActive = getSetOnItems(event.before)
        if (active == prevActive) {
            return
        }

        val key = when {
            ArmorUtils.isWearingAdvanced(player) -> "advanced-equip-sound"
            else -> "equip-sound"
        }

        val soundString = active.config.getStringOrNull(key) ?: return

        val soundSplit = soundString.split("::")
        if (soundSplit.size < 3) {
            return
        }

        try {
            player.world.playSound(player.location, Sound.valueOf(soundSplit[0].uppercase()), soundSplit[1].toFloat(), soundSplit[2].toFloat())
        } catch (ignored: IllegalArgumentException) {
            ignored.printStackTrace()
        } catch (ignored: NullPointerException) {
            ignored.printStackTrace()
        }
    }

    private fun getSetOnItems(items: List<ItemStack?>): ArmorSet? {
        var set: ArmorSet? = null
        for (item in items) {
            if (item == null) return null
            if (item.type.isAir) return null
            if (ArmorUtils.getSetOnItem(item) == null) return null
            if (set != null && set != ArmorUtils.getSetOnItem(item)) return null
            set = ArmorUtils.getSetOnItem(item)
        }
        return set
    }
}