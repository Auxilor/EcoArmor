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

            return getSlot(material.name)
        }

        @JvmStatic
        fun getSlot(name: String): ArmorSlot? {
            return when {
                name.contains("HELMET", true) || name.contains("HEAD", true)
                        || name.contains("SKULL", true)
                        || name.contains("PUMPKIN", true) -> HELMET
                name.contains("CHESTPLATE", true) -> CHESTPLATE
                name.contains("ELYTRA", true) -> ELYTRA
                name.contains("LEGGINGS", true) -> LEGGINGS
                name.contains("BOOTS", true) -> BOOTS
                else -> null
            }
        }
    }
}