package com.willfp.ecoarmor.sets.meta;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ArmorSlot {
    /**
     * Helmet.
     */
    HELMET,

    /**
     * Chestplate.
     */
    CHESTPLATE,

    /**
     * Elytra.
     */
    ELYTRA,

    /**
     * Leggings.
     */
    LEGGINGS,

    /**
     * Boots.
     */
    BOOTS;

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
