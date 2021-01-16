package com.willfp.ecoarmor.sets.meta;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ArmorSlot {
    /**
     * Helmet.
     */
    HELMET(EquipmentSlot.HEAD),

    /**
     * Chestplate.
     */
    CHESTPLATE(EquipmentSlot.CHEST),

    /**
     * Elytra.
     */
    ELYTRA(EquipmentSlot.CHEST),

    /**
     * Leggings.
     */
    LEGGINGS(EquipmentSlot.LEGS),

    /**
     * Boots.
     */
    BOOTS(EquipmentSlot.FEET);

    /**
     * The equipment slot.
     */
    @Getter
    private final EquipmentSlot slot;

    ArmorSlot(@NotNull final EquipmentSlot slot) {
        this.slot = slot;
    }

    /**
     * Get ArmorSlot from item.
     *
     * @param itemStack The item.
     * @return The slot, or null.
     */
    @Nullable
    public static ArmorSlot getSlot(@NotNull final ItemStack itemStack) {
        Material material = itemStack.getType();
        String name = material.name().toLowerCase();

        if (name.endsWith("helmet") || name.endsWith("head")) {
            return HELMET;
        }

        if (name.endsWith("chestplate")) {
            return CHESTPLATE;
        }

        if (name.endsWith("elytra")) {
            return ELYTRA;
        }

        if (name.endsWith("leggings")) {
            return LEGGINGS;
        }

        if (name.endsWith("boots")) {
            return BOOTS;
        }

        return null;
    }
}
