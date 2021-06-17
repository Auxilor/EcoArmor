package com.willfp.ecoarmor.sets.util;

import com.willfp.ecoarmor.EcoArmorPlugin;
import com.willfp.ecoarmor.conditions.Condition;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.ArmorSets;
import com.willfp.ecoarmor.sets.meta.ArmorSlot;
import com.willfp.ecoarmor.upgrades.Tier;
import com.willfp.ecoarmor.upgrades.Tiers;
import lombok.experimental.UtilityClass;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class ArmorUtils {
    /**
     * Instance of EcoArmor.
     */
    private static final EcoArmorPlugin PLUGIN = EcoArmorPlugin.getInstance();

    /**
     * Get armor set on an item.
     *
     * @param itemStack The itemStack to check.
     * @return The set, or null if no set is found.
     */
    @Nullable
    public ArmorSet getSetOnItem(@NotNull final ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) {
            return null;
        }

        return getSetOnItem(meta);
    }

    /**
     * Get armor set on an item.
     *
     * @param meta The itemStack to check.
     * @return The set, or null if no set is found.
     */
    @Nullable
    public ArmorSet getSetOnItem(@NotNull final ItemMeta meta) {
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String setName = container.get(PLUGIN.getNamespacedKeyFactory().create("set"), PersistentDataType.STRING);

        if (setName == null) {
            return null;
        }

        return ArmorSets.getByName(setName);
    }

    /**
     * Get armor set that player is wearing.
     *
     * @param player The player to check.
     * @return The set, or null if no full set is worn.
     */
    @Nullable
    public ArmorSet getSetOnPlayer(@NotNull final Player player) {
        List<ArmorSet> found = new ArrayList<>();

        for (ItemStack itemStack : player.getInventory().getArmorContents()) {
            if (itemStack == null) {
                continue;
            }

            ArmorSet set = getSetOnItem(itemStack);

            if (set == null) {
                continue;
            }

            found.add(set);
        }

        if (found.size() < 4) {
            return null;
        }

        boolean allEqual = true;
        for (ArmorSet set : found) {
            if (!set.equals(found.get(0))) {
                allEqual = false;
                break;
            }
        }

        if (allEqual) {
            return found.get(0);
        }

        return null;
    }

    /**
     * Get if all conditions are met for a player.
     *
     * @param player The player.
     * @return If conditions are men.
     */
    public boolean areConditionsMet(@NotNull final Player player) {
        ArmorSet set = getSetOnPlayer(player);
        if (set == null) {
            return true;
        }

        for (Map.Entry<Condition<?>, Object> entry : set.getConditions().entrySet()) {
            if (!entry.getKey().isMet(player, entry.getValue())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get tier on upgrade crystal.
     *
     * @param itemStack The item to check.
     * @return The found tier, or null.
     */
    @Nullable
    public static Tier getCrystalTier(@NotNull final ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) {
            return null;
        }

        return getCrystalTier(meta);
    }

    /**
     * Get tier on upgrade crystal.
     *
     * @param meta The item to check.
     * @return The found tier, or null.
     */
    @Nullable
    public static Tier getCrystalTier(@NotNull final ItemMeta meta) {
        if (meta.getPersistentDataContainer().has(PLUGIN.getNamespacedKeyFactory().create("upgrade_crystal"), PersistentDataType.STRING)) {
            return Tiers.getByName(meta.getPersistentDataContainer().get(PLUGIN.getNamespacedKeyFactory().create("upgrade_crystal"), PersistentDataType.STRING));
        }

        return null;
    }

    /**
     * Get tier on item.
     *
     * @param itemStack The item to check.
     * @return The found tier, or null if not found.
     */
    @Nullable
    public static Tier getTier(@NotNull final ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) {
            return null;
        }

        Tier tier = getTier(meta);

        if (getSetOnItem(meta) != null && tier == null) {
            setTier(itemStack, Tiers.getDefaultTier());
            return Tiers.getDefaultTier();
        } else {
            return tier;
        }
    }

    /**
     * Get tier on item.
     *
     * @param meta The item to check.
     * @return The found tier, or null if not found.
     */
    @Nullable
    public static Tier getTier(@NotNull final ItemMeta meta) {
        if (getSetOnItem(meta) == null) {
            return null;
        }

        if (meta.getPersistentDataContainer().has(PLUGIN.getNamespacedKeyFactory().create("tier"), PersistentDataType.STRING)) {
            return Tiers.getByName(meta.getPersistentDataContainer().get(PLUGIN.getNamespacedKeyFactory().create("tier"), PersistentDataType.STRING));
        }

        return null;
    }

    /**
     * Set tier on item.
     *
     * @param itemStack The item to check.
     * @param tier      The tier to set.
     */
    public static void setTier(@NotNull final ItemStack itemStack,
                               @NotNull final Tier tier) {
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) {
            return;
        }

        if (!meta.getPersistentDataContainer().has(PLUGIN.getNamespacedKeyFactory().create("set"), PersistentDataType.STRING)) {
            return;
        }

        meta.getPersistentDataContainer().set(PLUGIN.getNamespacedKeyFactory().create("tier"), PersistentDataType.STRING, tier.getName());

        ArmorSlot slot = ArmorSlot.getSlot(itemStack);

        if (slot == null) {
            return;
        }

        int armor = tier.getProperties().get(slot).getArmor();
        int toughness = tier.getProperties().get(slot).getToughness();
        int knockback = tier.getProperties().get(slot).getKnockback();
        int speed = tier.getProperties().get(slot).getSpeed();
        int attackSpeed = tier.getProperties().get(slot).getAttackSpeed();
        int attackDamage = tier.getProperties().get(slot).getAttackDamage();
        int attackKnockback = tier.getProperties().get(slot).getAttackKnockback();
        meta.removeAttributeModifier(Attribute.GENERIC_ARMOR);
        meta.removeAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS);
        meta.removeAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        meta.removeAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED);
        meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);
        meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
        meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_KNOCKBACK);

        if (armor > 0) {
            meta.addAttributeModifier(
                    Attribute.GENERIC_ARMOR,
                    new AttributeModifier(UUID.randomUUID(), "ecoarmor-armor", armor, AttributeModifier.Operation.ADD_NUMBER, slot.getSlot())
            );
        }
        if (toughness > 0) {
            meta.addAttributeModifier(
                    Attribute.GENERIC_ARMOR_TOUGHNESS,
                    new AttributeModifier(UUID.randomUUID(), "ecoarmor-toughness", toughness, AttributeModifier.Operation.ADD_NUMBER, slot.getSlot())
            );
        }
        if (knockback > 0) {
            meta.addAttributeModifier(
                    Attribute.GENERIC_KNOCKBACK_RESISTANCE,
                    new AttributeModifier(UUID.randomUUID(), "ecoarmor-knockback", (double) knockback / 10, AttributeModifier.Operation.ADD_NUMBER, slot.getSlot())
            );
        }
        if (speed != 0) {
            meta.addAttributeModifier(
                    Attribute.GENERIC_MOVEMENT_SPEED,
                    new AttributeModifier(UUID.randomUUID(), "ecoarmor-speed", (double) speed / 100, AttributeModifier.Operation.ADD_SCALAR, slot.getSlot())
            );
        }
        if (attackSpeed != 0) {
            meta.addAttributeModifier(
                    Attribute.GENERIC_ATTACK_SPEED,
                    new AttributeModifier(UUID.randomUUID(), "ecoarmor-attackspeed", (double) attackSpeed / 100, AttributeModifier.Operation.ADD_SCALAR, slot.getSlot())
            );
        }
        if (attackDamage != 0) {
            meta.addAttributeModifier(
                    Attribute.GENERIC_ATTACK_DAMAGE,
                    new AttributeModifier(UUID.randomUUID(), "ecoarmor-attackdamage", (double) attackDamage / 100, AttributeModifier.Operation.ADD_SCALAR, slot.getSlot())
            );
        }
        if (attackKnockback != 0) {
            meta.addAttributeModifier(
                    Attribute.GENERIC_ATTACK_KNOCKBACK,
                    new AttributeModifier(UUID.randomUUID(), "ecoarmor-attackknockback", (double) attackKnockback / 100, AttributeModifier.Operation.ADD_SCALAR, slot.getSlot())
            );
        }

        itemStack.setItemMeta(meta);
    }

    /**
     * Get if player is wearing advanced set.
     *
     * @param player The player to check.
     * @return If advanced.
     */
    public static boolean isWearingAdvanced(@NotNull final Player player) {
        if (getSetOnPlayer(player) == null) {
            return false;
        }

        for (ItemStack itemStack : player.getInventory().getArmorContents()) {
            if (itemStack == null) {
                return false;
            }

            if (!isAdvanced(itemStack)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get if item is advanced.
     *
     * @param itemStack The item to check.
     * @return If advanced.
     */
    public static boolean isAdvanced(@NotNull final ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) {
            return false;
        }

        return isAdvanced(meta);
    }

    /**
     * Get if item is advanced.
     *
     * @param meta The item to check.
     * @return If advanced.
     */
    public static boolean isAdvanced(@NotNull final ItemMeta meta) {
        if (meta.getPersistentDataContainer().has(PLUGIN.getNamespacedKeyFactory().create("advanced"), PersistentDataType.INTEGER)) {
            return meta.getPersistentDataContainer().get(PLUGIN.getNamespacedKeyFactory().create("advanced"), PersistentDataType.INTEGER) == 1;
        }

        return false;
    }

    /**
     * Set if item is advanced.
     *
     * @param itemStack The item to set.
     * @param advanced  If the item should be advanced.
     */
    public static void setAdvanced(@NotNull final ItemStack itemStack,
                                   final boolean advanced) {
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) {
            return;
        }

        if (getSetOnItem(itemStack) == null) {
            return;
        }

        meta.getPersistentDataContainer().set(PLUGIN.getNamespacedKeyFactory().create("advanced"), PersistentDataType.INTEGER, advanced ? 1 : 0);

        itemStack.setItemMeta(meta);
    }

    /**
     * Get the set from a shard.
     *
     * @param itemStack The item to check.
     * @return The set, or null if not a shard.
     */
    @Nullable
    public static ArmorSet getShardSet(@NotNull final ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) {
            return null;
        }

        return getShardSet(meta);
    }

    /**
     * Get the set from a shard.
     *
     * @param meta The item to check.
     * @return The set, or null if not a shard.
     */
    @Nullable
    public static ArmorSet getShardSet(@NotNull final ItemMeta meta) {
        String shardSet = meta.getPersistentDataContainer().get(PLUGIN.getNamespacedKeyFactory().create("advancement-shard"), PersistentDataType.STRING);

        if (shardSet == null) {
            return null;
        }

        return ArmorSets.getByName(shardSet);
    }

    /**
     * Get value of condition.
     *
     * @param string    Value as string.
     * @param condition Condition.
     * @param <T>       The type of the condition.
     * @return Value.
     */
    @NotNull
    public static <T> Object getConditionValue(@NotNull final String string,
                                               @NotNull final Condition<T> condition) {
        if (condition.getTypeClass().equals(Boolean.class)) {
            return Boolean.parseBoolean(string);
        }

        if (condition.getTypeClass().equals(Integer.class)) {
            return Integer.parseInt(string);
        }

        if (condition.getTypeClass().equals(Double.class)) {
            return Double.parseDouble(string);
        }

        return string;
    }
}
