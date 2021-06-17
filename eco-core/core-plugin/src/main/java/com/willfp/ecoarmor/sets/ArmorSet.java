package com.willfp.ecoarmor.sets;

import com.willfp.ecoarmor.conditions.Condition;
import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.sets.meta.ArmorSlot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
@SuppressWarnings("unchecked")
public class ArmorSet {
    /**
     * The name of the set.
     */
    @Getter
    private final String name;

    /**
     * Conditions and their values.
     */
    @Getter
    private final Map<Condition<?>, Object> conditions;

    /**
     * Effects and their strengths.
     */
    @Getter
    private final Map<Effect<?>, Object> effects;

    /**
     * Effects and their strengths on advanced armor.
     */
    @Getter
    private final Map<Effect<?>, Object> advancedEffects;

    /**
     * Potion effects to be applied on equip.
     */
    @Getter
    private final Map<PotionEffectType, Integer> potionEffects;

    /**
     * Potion effects to be applied on equipping advanced.
     */
    @Getter
    private final Map<PotionEffectType, Integer> advancedPotionEffects;

    /**
     * The base64 texture of a skull used as a helmet.
     * <p>
     * Null if no skull.
     */
    @Getter
    @Nullable
    private final String skullBase64;

    /**
     * Items in set.
     */
    private final Map<ArmorSlot, ItemStack> items;

    /**
     * Items in advanced set.
     */
    private final Map<ArmorSlot, ItemStack> advancedItems;

    /**
     * Advancement shard item.
     */
    @Getter
    private final ItemStack advancementShardItem;

    /**
     * Get item stack from slot.
     *
     * @param slot The slot.
     * @return The item.
     */
    public ItemStack getItemStack(@NotNull final ArmorSlot slot) {
        return items.get(slot);
    }

    /**
     * Get item stack from slot.
     *
     * @param slot The slot.
     * @return The item.
     */
    public ItemStack getAdvancedItemStack(@NotNull final ArmorSlot slot) {
        return advancedItems.get(slot);
    }

    /**
     * Get condition value of effect.
     *
     * @param condition The condition to query.
     * @param <T>       The type of the condition value.
     * @return The value.
     */
    @Nullable
    public <T> T getConditionValue(@NotNull final Condition<T> condition) {
        return (T) conditions.get(condition);
    }

    /**
     * Get effect strength of effect.
     *
     * @param effect The effect to query.
     * @param <T>    The type of the effect value.
     * @return The strength.
     */
    @Nullable
    public <T> T getEffectStrength(@NotNull final Effect<T> effect) {
        return (T) effects.get(effect);
    }

    /**
     * Get effect strength of effect on advanced armor.
     *
     * @param effect The effect to query.
     * @param <T>    The type of the effect value.
     * @return The strength.
     */
    @Nullable
    public <T> T getAdvancedEffectStrength(@NotNull final Effect<T> effect) {
        Object strength = advancedEffects.get(effect);
        if (strength instanceof Integer) {
            if (effect.getTypeClass().equals(Double.class)) {
                strength = (double) (int) strength;
            }
        }

        return (T) strength;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ArmorSet set)) {
            return false;
        }

        return this.name.equals(set.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    @Override
    public String toString() {
        return "ArmorSet{"
                + this.name
                + "}";
    }
}
