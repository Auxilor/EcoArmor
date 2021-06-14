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
    public static ArmorSlot getSlot(@Nullable final ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        Material material = itemStack.getType();
        String[] split = material.name().toLowerCase().split("_");
        String name = split[split.length - 1];

        return switch (name) {
            case "helmet", "head" -> HELMET;
            case "chestplate" -> CHESTPLATE;
            case "elytra" -> ELYTRA;
            case "leggings" -> LEGGINGS;
            case "boots" -> BOOTS;
            default -> null;
        };
    }

    /**
     * Get ArmorSlot from name.
     *
     * @param name The name.
     * @return The slot, or null.
     */
    @Nullable
    public static ArmorSlot getSlot(@NotNull final String name) {
        return switch (name.toLowerCase()) {
            case "helmet" -> HELMET;
            case "chestplate" -> CHESTPLATE;
            case "elytra" -> ELYTRA;
            case "leggings" -> LEGGINGS;
            case "boots" -> BOOTS;
            default -> null;
        };
    }
}
