package com.willfp.ecoarmor.sets

import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.*

enum class ArmorSlot(
    val slot: EquipmentSlot
) {
    HELMET(EquipmentSlot.HEAD),
    CHESTPLATE(EquipmentSlot.CHEST),
    ELYTRA(EquipmentSlot.CHEST),
    LEGGINGS(EquipmentSlot.LEGS),
    BOOTS(EquipmentSlot.FEET);

    companion object {
        @JvmStatic
        fun getSlot(itemStack: ItemStack?): ArmorSlot? {
            if (itemStack == null) {
                return null
            }
            val material = itemStack.type
            val split = material.name.lowercase(Locale.getDefault()).split("_").toTypedArray()

            return getSlot(split[split.size - 1])
        }

        @JvmStatic
        fun getSlot(name: String): ArmorSlot? {
            return when (name.lowercase(Locale.getDefault())) {
                "helmet" -> HELMET
                "chestplate" -> CHESTPLATE
                "elytra" -> ELYTRA
                "leggings" -> LEGGINGS
                "boots" -> BOOTS
                else -> null
            }
        }
    }
}