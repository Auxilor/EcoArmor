package com.willfp.ecoarmor.sets

import com.willfp.ecoarmor.EcoArmorPlugin.Companion.instance
import com.willfp.ecoarmor.sets.ArmorSlot.Companion.getSlot
import com.willfp.ecoarmor.upgrades.Tier
import com.willfp.ecoarmor.upgrades.Tiers
import com.willfp.libreforge.Holder
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import java.util.*

object ArmorUtils {
    /**
     * Instance of EcoArmor.
     */
    private val PLUGIN = instance

    /**
     * Get armor set on an item.
     *
     * @param itemStack The itemStack to check.
     * @return The set, or null if no set is found.
     */
    @JvmStatic
    fun getSetOnItem(itemStack: ItemStack): ArmorSet? {
        val meta = itemStack.itemMeta ?: return null
        return getSetOnItem(meta)
    }

    /**
     * Get armor set on an item.
     *
     * @param meta The itemStack to check.
     * @return The set, or null if no set is found.
     */
    @JvmStatic
    fun getSetOnItem(meta: ItemMeta): ArmorSet? {
        val container = meta.persistentDataContainer
        val setName = container.get(
            PLUGIN.namespacedKeyFactory.create("set"),
            PersistentDataType.STRING
        )
            ?: return null
        return ArmorSets.getByID(setName)
    }

    /**
     * Get active holder for a player.
     *
     * @param player The player to check.
     * @return The holder, or null if not found.
     */
    @JvmStatic
    fun getActiveSet(player: Player): Holder? {
        val armorSet = getSetOnPlayer(player)
        val advanced = isWearingAdvanced(player)
        return if (armorSet != null) {
            if (advanced) armorSet.advancedHolder else armorSet.regularHolder
        } else {
            null
        }
    }

    /**
     * Get armor set that player is wearing.
     *
     * @param player The player to check.
     * @return The set, or null if no full set is worn.
     */
    @JvmStatic
    fun getSetOnPlayer(player: Player): ArmorSet? {
        val found: MutableList<ArmorSet> = ArrayList()
        for (itemStack in player.inventory.armorContents) {
            if (itemStack == null) {
                continue
            }
            val set = getSetOnItem(itemStack) ?: continue
            found.add(set)
        }
        if (found.size < 4) {
            return null
        }
        var allEqual = true
        for (set in found) {
            if (set != found[0]) {
                allEqual = false
                break
            }
        }
        return if (allEqual) {
            found[0]
        } else null
    }

    /**
     * Get tier on upgrade crystal.
     *
     * @param itemStack The item to check.
     * @return The found tier, or null.
     */
    @JvmStatic
    fun getCrystalTier(itemStack: ItemStack): Tier? {
        val meta = itemStack.itemMeta ?: return null
        return getCrystalTier(meta)
    }

    /**
     * Get tier on upgrade crystal.
     *
     * @param meta The item to check.
     * @return The found tier, or null.
     */
    @JvmStatic
    fun getCrystalTier(meta: ItemMeta): Tier? {
        return if (meta.persistentDataContainer.has(
                PLUGIN.namespacedKeyFactory.create(
                    "upgrade_crystal"
                ), PersistentDataType.STRING
            )
        ) {
            Tiers.getByID(
                meta.persistentDataContainer.get(
                    PLUGIN.namespacedKeyFactory.create(
                        "upgrade_crystal"
                    ), PersistentDataType.STRING
                )
            )
        } else null
    }

    /**
     * Get tier on item.
     *
     * @param itemStack The item to check.
     * @return The found tier, or null if not found.
     */
    @JvmStatic
    fun getTier(itemStack: ItemStack): Tier? {
        val meta = itemStack.itemMeta ?: return null
        val tier = getTier(meta)
        return if (getSetOnItem(meta) != null && tier == null) {
            setTier(itemStack, Tiers.getDefaultTier())
            Tiers.getDefaultTier()
        } else {
            tier
        }
    }

    /**
     * Get tier on item.
     *
     * @param meta The item to check.
     * @return The found tier, or null if not found.
     */
    @JvmStatic
    fun getTier(meta: ItemMeta): Tier? {
        if (getSetOnItem(meta) == null) {
            return null
        }
        return if (meta.persistentDataContainer.has(
                PLUGIN.namespacedKeyFactory.create(
                    "tier"
                ), PersistentDataType.STRING
            )
        ) {
            Tiers.getByID(
                meta.persistentDataContainer.get(
                    PLUGIN.namespacedKeyFactory.create(
                        "tier"
                    ), PersistentDataType.STRING
                )
            )
        } else null
    }

    /**
     * Set tier on item.
     *
     * @param itemStack The item to check.
     * @param tier      The tier to set.
     */
    @JvmStatic
    fun setTier(
        itemStack: ItemStack,
        tier: Tier
    ) {
        val meta = itemStack.itemMeta ?: return
        if (!meta.persistentDataContainer.has(
                PLUGIN.namespacedKeyFactory.create("set"),
                PersistentDataType.STRING
            )
        ) {
            return
        }
        meta.persistentDataContainer.set(
            PLUGIN.namespacedKeyFactory.create("tier"),
            PersistentDataType.STRING,
            tier.id
        )
        val slot = getSlot(itemStack) ?: return
        val armor = tier.properties[slot]!!.armor()
        val toughness = tier.properties[slot]!!.toughness()
        val knockback = tier.properties[slot]!!.knockback()
        val speed = tier.properties[slot]!!.speed()
        val attackSpeed = tier.properties[slot]!!.attackSpeed()
        val attackDamage = tier.properties[slot]!!.attackDamage()
        val attackKnockback = tier.properties[slot]!!.attackKnockback()
        meta.removeAttributeModifier(Attribute.GENERIC_ARMOR)
        meta.removeAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS)
        meta.removeAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE)
        meta.removeAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED)
        meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED)
        meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE)
        meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_KNOCKBACK)
        if (armor > 0) {
            meta.addAttributeModifier(
                Attribute.GENERIC_ARMOR,
                AttributeModifier(
                    UUID.randomUUID(),
                    "ecoarmor-armor",
                    armor.toDouble(),
                    AttributeModifier.Operation.ADD_NUMBER,
                    slot.slot
                )
            )
        }
        if (toughness > 0) {
            meta.addAttributeModifier(
                Attribute.GENERIC_ARMOR_TOUGHNESS,
                AttributeModifier(
                    UUID.randomUUID(),
                    "ecoarmor-toughness",
                    toughness.toDouble(),
                    AttributeModifier.Operation.ADD_NUMBER,
                    slot.slot
                )
            )
        }
        if (knockback > 0) {
            meta.addAttributeModifier(
                Attribute.GENERIC_KNOCKBACK_RESISTANCE,
                AttributeModifier(
                    UUID.randomUUID(),
                    "ecoarmor-knockback",
                    knockback.toDouble() / 10,
                    AttributeModifier.Operation.ADD_NUMBER,
                    slot.slot
                )
            )
        }
        if (speed != 0) {
            meta.addAttributeModifier(
                Attribute.GENERIC_MOVEMENT_SPEED,
                AttributeModifier(
                    UUID.randomUUID(),
                    "ecoarmor-speed",
                    speed.toDouble() / 100,
                    AttributeModifier.Operation.ADD_SCALAR,
                    slot.slot
                )
            )
        }
        if (attackSpeed != 0) {
            meta.addAttributeModifier(
                Attribute.GENERIC_ATTACK_SPEED,
                AttributeModifier(
                    UUID.randomUUID(),
                    "ecoarmor-attackspeed",
                    attackSpeed.toDouble() / 100,
                    AttributeModifier.Operation.ADD_SCALAR,
                    slot.slot
                )
            )
        }
        if (attackDamage != 0) {
            meta.addAttributeModifier(
                Attribute.GENERIC_ATTACK_DAMAGE,
                AttributeModifier(
                    UUID.randomUUID(),
                    "ecoarmor-attackdamage",
                    attackDamage.toDouble() / 100,
                    AttributeModifier.Operation.ADD_SCALAR,
                    slot.slot
                )
            )
        }
        if (attackKnockback != 0) {
            meta.addAttributeModifier(
                Attribute.GENERIC_ATTACK_KNOCKBACK,
                AttributeModifier(
                    UUID.randomUUID(),
                    "ecoarmor-attackknockback",
                    attackKnockback.toDouble() / 100,
                    AttributeModifier.Operation.ADD_SCALAR,
                    slot.slot
                )
            )
        }
        itemStack.itemMeta = meta
    }

    /**
     * Get if player is wearing advanced set.
     *
     * @param player The player to check.
     * @return If advanced.
     */
    @JvmStatic
    fun isWearingAdvanced(player: Player): Boolean {
        if (getSetOnPlayer(player) == null) {
            return false
        }
        for (itemStack in player.inventory.armorContents) {
            if (itemStack == null) {
                return false
            }
            if (!isAdvanced(itemStack)) {
                return false
            }
        }
        return true
    }

    /**
     * Get if item is advanced.
     *
     * @param itemStack The item to check.
     * @return If advanced.
     */
    @JvmStatic
    fun isAdvanced(itemStack: ItemStack): Boolean {
        val meta = itemStack.itemMeta ?: return false
        return isAdvanced(meta)
    }

    /**
     * Get if item is advanced.
     *
     * @param meta The item to check.
     * @return If advanced.
     */
    @JvmStatic
    fun isAdvanced(meta: ItemMeta): Boolean {
        return if (meta.persistentDataContainer.has(
                PLUGIN.namespacedKeyFactory.create("advanced"),
                PersistentDataType.INTEGER
            )
        ) {
            meta.persistentDataContainer.get(
                PLUGIN.namespacedKeyFactory.create("advanced"),
                PersistentDataType.INTEGER
            ) == 1
        } else false
    }

    /**
     * Set if item is advanced.
     *
     * @param itemStack The item to set.
     * @param advanced  If the item should be advanced.
     */
    @JvmStatic
    fun setAdvanced(
        itemStack: ItemStack,
        advanced: Boolean
    ) {
        val meta = itemStack.itemMeta ?: return
        meta.persistentDataContainer.set(
            PLUGIN.namespacedKeyFactory.create("advanced"),
            PersistentDataType.INTEGER,
            if (advanced) 1 else 0
        )
        itemStack.itemMeta = meta
    }

    /**
     * Get the set from a shard.
     *
     * @param itemStack The item to check.
     * @return The set, or null if not a shard.
     */
    @JvmStatic
    fun getShardSet(itemStack: ItemStack): ArmorSet? {
        val meta = itemStack.itemMeta ?: return null
        return getShardSet(meta)
    }

    /**
     * Get the set from a shard.
     *
     * @param meta The item to check.
     * @return The set, or null if not a shard.
     */
    @JvmStatic
    fun getShardSet(meta: ItemMeta): ArmorSet? {
        val shardSet = meta.persistentDataContainer.get(
            PLUGIN.namespacedKeyFactory.create(
                "advancement-shard"
            ), PersistentDataType.STRING
        )
            ?: return null
        return ArmorSets.getByID(shardSet)
    }
}