package com.willfp.ecoarmor.sets

import com.willfp.ecoarmor.api.event.PlayerArmorSetEquipEvent
import com.willfp.ecoarmor.api.event.PlayerArmorSetUnequipEvent
import com.willfp.ecoarmor.plugin
import com.willfp.ecoarmor.sets.ArmorSlot.Companion.getSlot
import com.willfp.ecoarmor.upgrades.Tier
import com.willfp.ecoarmor.upgrades.TierProperties
import com.willfp.ecoarmor.upgrades.Tiers
import com.willfp.libreforge.Holder
import com.willfp.libreforge.ItemProvidedHolder
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.SimpleProvidedHolder
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import java.util.*

object ArmorUtils {

    private data class CachedSetState(val set: ArmorSet?, val advanced: Boolean)

    /**
     * Cache of sets on players.
     */
    private val setCache = WeakHashMap<Player, CachedSetState>()

    /**
     * Remove a player from the set cache. Called on quit.
     */
    @JvmStatic
    fun removeFromCache(player: Player) {
        setCache.remove(player)
    }

    /**
     * Clear the player set cache. Called on reload/disable.
     */
    @JvmStatic
    fun clearCache() {
        setCache.clear()
    }

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
            plugin.namespacedKeyFactory.create("set"),
            PersistentDataType.STRING
        )
            ?: return null
        return ArmorSets.getByID(setName)
    }


    /**
     * Get active holder for an entity.
     *
     * @param entity The entity to check.
     * @return The holder, or null if not found.
     */
    @JvmStatic
    fun getActiveSet(entity: LivingEntity): Holder? {
        val armorSet = getSetOnEntity(entity)
        val advanced = isWearingAdvanced(entity)
        return if (armorSet != null) {
            if (advanced) armorSet.advancedHolder else armorSet.regularHolder
        } else {
            null
        }
    }


    /**
     * Get all active armor holders for an entity.
     *
     * @param entity The player.
     * @return The holders.
     */
    fun getActiveHolders(entity: LivingEntity): Collection<ProvidedHolder> {
        val holders = mutableListOf<ProvidedHolder>()

        val equipment = entity.equipment?.armorContents?.toList() ?: emptyList()

        // Pre-compute set lookups once for all items to avoid repeated PDC reads
        val itemSets = equipment.map { item -> item?.let { getSetOnItem(it) } }

        val fullSet = getSetOn(equipment, itemSets)
        val advanced = isWearingAdvanced(equipment, fullSet, itemSets)

        val set = if (fullSet != null) {
            if (advanced) fullSet.advancedHolder else fullSet.regularHolder
        } else {
            null
        }

        if (set != null) {
            holders.add(SimpleProvidedHolder(set))
        }

        val partialSetsWorn = countPartialSets(itemSets)
        for ((partialSet, count) in partialSetsWorn) {
            val suppressedByFull = fullSet != null && partialSet == fullSet && partialSet.fullSetDisablesPartialSet
            if (suppressedByFull) continue

            if (partialSet.partialHolders.isNotEmpty()) {
                if (partialSet.stackedPartialSets) {
                    for (requiredCount in partialSet.partialHolders.keys) {
                        if (count >= requiredCount) {
                            val holder = partialSet.partialHolders[requiredCount]
                            if (holder != null) holders.add(SimpleProvidedHolder(holder))
                        }
                    }
                } else {
                    val highestApplicableCount = partialSet.partialHolders.keys.filter { it <= count }.maxOrNull()
                    if (highestApplicableCount != null) {
                        val holder = partialSet.partialHolders[highestApplicableCount]
                        if (holder != null) holders.add(SimpleProvidedHolder(holder))
                    }
                }
            }
        }

        holders.addAll(getSlotHolders(equipment, itemSets))

        if (entity is Player) {
            val oldState = setCache[entity]
            val newSet = set?.armorSet
            val newState = CachedSetState(newSet, advanced)

            if (oldState?.set != newSet || oldState?.advanced != advanced) {
                // Update cache immediately so subsequent calls see correct state
                setCache[entity] = newState

                // Defer event firing to next tick to avoid re-entrancy from listeners
                val player = entity
                val oldAdvanced = oldState?.advanced ?: false
                plugin.scheduler.run {
                    if (!player.isOnline) return@run

                    if (oldState?.set != null) {
                        plugin.server.pluginManager.callEvent(
                            PlayerArmorSetUnequipEvent(player, oldState.set, oldAdvanced)
                        )
                    }
                    if (newSet != null) {
                        plugin.server.pluginManager.callEvent(
                            PlayerArmorSetEquipEvent(player, newSet, advanced)
                        )
                    }
                }
            }
        }

        return holders
    }


    /**
     * Get slot holders from pre-computed item sets.
     */
    private fun getSlotHolders(
        equipment: List<ItemStack?>,
        itemSets: List<ArmorSet?>
    ): Collection<ItemProvidedHolder> {
        val holders = mutableListOf<ItemProvidedHolder>()

        for (i in equipment.indices) {
            val itemStack = equipment[i] ?: continue
            val set = itemSets[i] ?: continue
            val holder = set.getSpecificHolder(itemStack) ?: continue
            holders.add(holder)
        }

        return holders
    }

    /**
     * Get the item an entity is wearing in an armor slot.
     *
     * Distinguishes elytras from chestplates, which share an equipment slot.
     *
     * @param entity The entity to check.
     * @param slot The slot to check.
     * @return The item, or null if the slot is empty or holds a different kind of item.
     */
    @JvmStatic
    fun getItemInSlot(entity: LivingEntity, slot: ArmorSlot): ItemStack? {
        val item = entity.equipment?.getItem(slot.slot) ?: return null
        if (item.type.isAir) {
            return null
        }
        return if (getSlot(item) == slot) item else null
    }

    /**
     * Get armor set that entity is wearing.
     *
     * @param entity The entity to check.
     * @return The set, or null if no full set is worn.
     */
    @JvmStatic
    fun getSetOnEntity(entity: LivingEntity): ArmorSet? {
        val equipment = entity.equipment?.armorContents?.toList() ?: return null
        return getSetOn(equipment)
    }

    /**
     * Get armor set that player is wearing.
     *
     * @param items The items to check.
     * @return The set, or null if no full set is worn.
     */
    @JvmStatic
    fun getSetOn(items: List<ItemStack?>): ArmorSet? {
        val found: MutableList<ArmorSet> = ArrayList()
        for (itemStack in items) {
            if (itemStack == null) {
                continue
            }
            val set = getSetOnItem(itemStack) ?: continue
            found.add(set)
        }
        return findFullSet(found)
    }

    /**
     * Get armor set from pre-computed per-item set lookups.
     */
    private fun getSetOn(items: List<ItemStack?>, itemSets: List<ArmorSet?>): ArmorSet? {
        val found = itemSets.filterNotNull()
        return findFullSet(found)
    }

    private fun findFullSet(found: List<ArmorSet>): ArmorSet? {
        if (found.isEmpty()) return null
        val grouped = found.groupingBy { it }.eachCount()
        for ((set, count) in grouped) {
            if (count >= set.setRequirements) {
                return set
            }
        }
        return null
    }

    /**
     * Get all armor sets that player is wearing a partial set of, paired with the amount they are wearing.
     *
     * @param items The items to check.
     * @return A map of sets to their worn amount.
     */
    @JvmStatic
    fun getPartialSetsOn(items: List<ItemStack?>): Map<ArmorSet, Int> {
        val found = mutableListOf<ArmorSet>()
        for (itemStack in items) {
            if (itemStack == null) continue
            val set = getSetOnItem(itemStack) ?: continue
            found.add(set)
        }
        if (found.isEmpty()) return emptyMap()
        return found.groupingBy { it }.eachCount()
    }

    /**
     * Get partial sets from pre-computed per-item set lookups.
     */
    private fun countPartialSets(itemSets: List<ArmorSet?>): Map<ArmorSet, Int> {
        val found = itemSets.filterNotNull()
        if (found.isEmpty()) return emptyMap()
        return found.groupingBy { it }.eachCount()
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
                plugin.namespacedKeyFactory.create(
                    "upgrade_crystal"
                ), PersistentDataType.STRING
            )
        ) {
            Tiers.getByID(
                meta.persistentDataContainer.get(
                    plugin.namespacedKeyFactory.create(
                        "upgrade_crystal"
                    ), PersistentDataType.STRING
                )
            )
        } else null
    }

    /**
     * Get the ids of every tier currently applied to an item, oldest first.
     *
     * For an item whose current tier is non-additive, this is a single-element
     * list matching [getTier]. For an item with additive tiers stacked, this
     * lists every tier applied so far, in application order (may contain
     * duplicates if the same additive tier was stacked more than once).
     *
     * @param meta The item to check.
     * @return The applied tier ids.
     */
    @JvmStatic
    fun getAppliedTierIds(meta: ItemMeta): List<String> {
        val stacked = meta.persistentDataContainer.get(
            plugin.namespacedKeyFactory.create("tiers"),
            PersistentDataType.STRING
        )
        if (stacked != null) {
            return stacked.split(",").filter { it.isNotBlank() }
        }
        val single = meta.persistentDataContainer.get(
            plugin.namespacedKeyFactory.create("tier"),
            PersistentDataType.STRING
        )
        return if (single != null) listOf(single) else emptyList()
    }

    /**
     * Get every tier currently applied to an item, oldest first.
     *
     * @param meta The item to check.
     * @return The applied tiers.
     */
    @JvmStatic
    fun getAppliedTiers(meta: ItemMeta): List<Tier> {
        return getAppliedTierIds(meta).mapNotNull { Tiers.getByID(it) }
    }

    /**
     * Check whether an additive tier can still be applied to an item without
     * exceeding its [Tier.stackLimit].
     *
     * @param itemStack The item to check.
     * @param tier The additive tier being applied.
     * @return True if applying would not exceed the tier's stack limit.
     */
    @JvmStatic
    fun canApplyAdditiveTier(itemStack: ItemStack, tier: Tier): Boolean {
        val meta = itemStack.itemMeta ?: return false
        if (tier.stackLimit < 0) return true
        val applied = getAppliedTierIds(meta).count { it == tier.id }
        return applied < tier.stackLimit
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
            setTier(itemStack, Tiers.defaultTier)
            Tiers.defaultTier
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
                plugin.namespacedKeyFactory.create(
                    "tier"
                ), PersistentDataType.STRING
            )
        ) {
            Tiers.getByID(
                meta.persistentDataContainer.get(
                    plugin.namespacedKeyFactory.create(
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
        val slot = getSlot(itemStack) ?: return
        val props = tier.properties[slot] ?: return

        if (tier.additive) {
            if (!canApplyAdditiveTier(itemStack, tier)) return

            val existingIds = getAppliedTierIds(meta).toMutableList()
            existingIds.add(tier.id)

            setTierKey(meta, tier)
            meta.persistentDataContainer.set(
                plugin.namespacedKeyFactory.create("tiers"),
                PersistentDataType.STRING,
                existingIds.joinToString(",")
            )

            removeAllTierAttributeModifiers(meta)

            val combinedProps = sumProperties(
                existingIds.mapNotNull { Tiers.getByID(it) }.mapNotNull { it.properties[slot] }
            )

            addTierModifiers(meta, slot, combinedProps)

            itemStack.itemMeta = meta
            return
        }

        meta.persistentDataContainer.remove(plugin.namespacedKeyFactory.create("tiers"))
        setTierKey(meta, tier)

        removeAllTierAttributeModifiers(meta)

        addTierModifiers(meta, slot, props)

        itemStack.itemMeta = meta
    }

    /**
     * Remove every attribute modifier this plugin may have added for a tier,
     * so stats can be recalculated from scratch.
     *
     * @param meta The meta to mutate.
     */
    private fun removeAllTierAttributeModifiers(meta: ItemMeta) {
        meta.removeAttributeModifier(Attribute.ARMOR)
        meta.removeAttributeModifier(Attribute.ARMOR_TOUGHNESS)
        meta.removeAttributeModifier(Attribute.KNOCKBACK_RESISTANCE)
        meta.removeAttributeModifier(Attribute.MOVEMENT_SPEED)
        meta.removeAttributeModifier(Attribute.ATTACK_SPEED)
        meta.removeAttributeModifier(Attribute.ATTACK_DAMAGE)
        meta.removeAttributeModifier(Attribute.ATTACK_KNOCKBACK)
        meta.removeAttributeModifier(Attribute.MAX_HEALTH)
        meta.removeAttributeModifier(Attribute.JUMP_STRENGTH)
        meta.removeAttributeModifier(Attribute.GRAVITY)
        meta.removeAttributeModifier(Attribute.BURNING_TIME)
        meta.removeAttributeModifier(Attribute.EXPLOSION_KNOCKBACK_RESISTANCE)
        meta.removeAttributeModifier(Attribute.OXYGEN_BONUS)
        meta.removeAttributeModifier(Attribute.MOVEMENT_EFFICIENCY)
        meta.removeAttributeModifier(Attribute.SAFE_FALL_DISTANCE)
        meta.removeAttributeModifier(Attribute.ENTITY_INTERACTION_RANGE)
        meta.removeAttributeModifier(Attribute.BLOCK_INTERACTION_RANGE)
    }

    /**
     * Sum a list of tier stat blocks into a single combined stat block, treating
     * unset fields as 0. Used so stacked additive tiers show one recalculated
     * attribute modifier per attribute instead of one per application.
     *
     * @param propsList The stat blocks to combine.
     * @return The combined stat block.
     */
    private fun sumProperties(propsList: List<TierProperties>): TierProperties {
        fun sum(selector: (TierProperties) -> Int?) = propsList.sumOf { selector(it) ?: 0 }

        return TierProperties(
            armor = sum { it.armor },
            toughness = sum { it.toughness },
            knockbackResistance = sum { it.knockbackResistance },
            speedPercentage = sum { it.speedPercentage },
            attackSpeedPercentage = sum { it.attackSpeedPercentage },
            attackDamagePercentage = sum { it.attackDamagePercentage },
            attackKnockbackPercentage = sum { it.attackKnockbackPercentage },
            maxHealth = sum { it.maxHealth },
            attackDamageFlat = sum { it.attackDamageFlat },
            attackSpeedFlat = sum { it.attackSpeedFlat },
            jumpStrength = sum { it.jumpStrength },
            gravityPercentage = sum { it.gravityPercentage },
            burningTimePercentage = sum { it.burningTimePercentage },
            explosionKnockbackResistance = sum { it.explosionKnockbackResistance },
            oxygenBonus = sum { it.oxygenBonus },
            movementEfficiency = sum { it.movementEfficiency },
            safeFallDistance = sum { it.safeFallDistance },
            entityInteractionRangePercentage = sum { it.entityInteractionRangePercentage },
            blockInteractionRangePercentage = sum { it.blockInteractionRangePercentage }
        )
    }

    /**
     * Attach a tier's attribute modifiers to an item's meta.
     *
     * Modifier keys are per-attribute-per-slot only (not per-tier), so callers
     * must pass the already-combined stat block for every tier applied to the
     * item, and clear any previous modifiers first via
     * [removeAllTierAttributeModifiers].
     *
     * @param meta The meta to mutate.
     * @param slot The armor slot the item occupies.
     * @param props The combined stat block for that slot.
     */
    private fun addTierModifiers(
        meta: ItemMeta,
        slot: ArmorSlot,
        props: TierProperties
    ) {
        val slotGroup = when (slot.slot) {
            org.bukkit.inventory.EquipmentSlot.HEAD -> EquipmentSlotGroup.HEAD
            org.bukkit.inventory.EquipmentSlot.CHEST -> EquipmentSlotGroup.CHEST
            org.bukkit.inventory.EquipmentSlot.LEGS -> EquipmentSlotGroup.LEGS
            org.bukkit.inventory.EquipmentSlot.FEET -> EquipmentSlotGroup.FEET
            else -> EquipmentSlotGroup.ANY
        }

        val slotSuffix = slot.name.lowercase(Locale.getDefault())

        fun addModifier(
            attr: Attribute,
            value: Int?,
            op: AttributeModifier.Operation,
            keySuffix: String = "",
            scaler: (Int) -> Double = { it.toDouble() }
        ) {
            value?.takeIf { it != 0 }?.let { v ->
                val keyName = "${attr.key.key}.${slotSuffix}${keySuffix}"
                meta.addAttributeModifier(
                    attr,
                    AttributeModifier(
                        NamespacedKey("ecoarmor", keyName),
                        scaler(v),
                        op,
                        slotGroup
                    )
                )
            }
        }

        addModifier(Attribute.ARMOR, props.armor, AttributeModifier.Operation.ADD_NUMBER)
        addModifier(Attribute.ARMOR_TOUGHNESS, props.toughness, AttributeModifier.Operation.ADD_NUMBER)
        addModifier(Attribute.MAX_HEALTH, props.maxHealth, AttributeModifier.Operation.ADD_NUMBER)
        addModifier(Attribute.ATTACK_DAMAGE, props.attackDamageFlat, AttributeModifier.Operation.ADD_NUMBER, ".flat")
        addModifier(Attribute.ATTACK_SPEED, props.attackSpeedFlat, AttributeModifier.Operation.ADD_NUMBER, ".flat")
        addModifier(Attribute.SAFE_FALL_DISTANCE, props.safeFallDistance, AttributeModifier.Operation.ADD_NUMBER)
        addModifier(Attribute.OXYGEN_BONUS, props.oxygenBonus, AttributeModifier.Operation.ADD_NUMBER)

        val pctScaler: (Int) -> Double = { it / 100.0 }
        addModifier(Attribute.MOVEMENT_SPEED, props.speedPercentage, AttributeModifier.Operation.ADD_SCALAR, ".pct", pctScaler)
        addModifier(Attribute.ATTACK_SPEED, props.attackSpeedPercentage, AttributeModifier.Operation.ADD_SCALAR, ".pct", pctScaler)
        addModifier(Attribute.ATTACK_DAMAGE, props.attackDamagePercentage, AttributeModifier.Operation.ADD_SCALAR, ".pct", pctScaler)
        addModifier(Attribute.ATTACK_KNOCKBACK, props.attackKnockbackPercentage, AttributeModifier.Operation.ADD_SCALAR, "", pctScaler)
        addModifier(Attribute.JUMP_STRENGTH, props.jumpStrength, AttributeModifier.Operation.ADD_SCALAR, "", pctScaler)
        addModifier(Attribute.GRAVITY, props.gravityPercentage, AttributeModifier.Operation.ADD_SCALAR, "", pctScaler)
        addModifier(Attribute.BURNING_TIME, props.burningTimePercentage, AttributeModifier.Operation.ADD_SCALAR, "", pctScaler)
        addModifier(Attribute.MOVEMENT_EFFICIENCY, props.movementEfficiency, AttributeModifier.Operation.ADD_SCALAR, "", pctScaler)
        addModifier(Attribute.ENTITY_INTERACTION_RANGE, props.entityInteractionRangePercentage, AttributeModifier.Operation.ADD_SCALAR, "", pctScaler)
        addModifier(Attribute.BLOCK_INTERACTION_RANGE, props.blockInteractionRangePercentage, AttributeModifier.Operation.ADD_SCALAR, "", pctScaler)

        val fracScaler: (Int) -> Double = { it / 100.0 }
        addModifier(Attribute.KNOCKBACK_RESISTANCE, props.knockbackResistance, AttributeModifier.Operation.ADD_NUMBER, "", fracScaler)
        addModifier(Attribute.EXPLOSION_KNOCKBACK_RESISTANCE, props.explosionKnockbackResistance, AttributeModifier.Operation.ADD_NUMBER, "", fracScaler)
    }

    /**
     * Set tier on item.
     *
     * @param meta The item to check.
     * @param tier      The tier to set.
     */
    @JvmStatic
    fun setTierKey(
        meta: ItemMeta,
        tier: Tier
    ) {
        if (!meta.persistentDataContainer.has(
                plugin.namespacedKeyFactory.create("set"),
                PersistentDataType.STRING
            )
        ) {
            return
        }
        meta.persistentDataContainer.set(
            plugin.namespacedKeyFactory.create("tier"),
            PersistentDataType.STRING,
            tier.id
        )
    }


    /**
     * Get if entity is wearing advanced set.
     *
     * @param entity The entity to check.
     * @return If advanced.
     */
    @JvmStatic
    fun isWearingAdvanced(entity: LivingEntity): Boolean {
        val equipment = entity.equipment?.armorContents?.toList() ?: return false
        return isWearingAdvanced(equipment)
    }

    /**
     * Get if player is wearing advanced set.
     *
     * @param items The items to check.
     * @return If advanced.
     */
    @JvmStatic
    fun isWearingAdvanced(items: List<ItemStack?>): Boolean {
        if (getSetOn(items) == null) {
            return false
        }
        for (itemStack in items) {
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
     * Check advanced status using pre-computed full set and item list (avoids redundant PDC reads).
     */
    private fun isWearingAdvanced(items: List<ItemStack?>, fullSet: ArmorSet?, itemSets: List<ArmorSet?>): Boolean {
        if (fullSet == null) return false
        for (itemStack in items) {
            if (itemStack == null) return false
            if (!isAdvanced(itemStack)) return false
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
                plugin.namespacedKeyFactory.create("advanced"),
                PersistentDataType.INTEGER
            )
        ) {
            meta.persistentDataContainer.get(
                plugin.namespacedKeyFactory.create("advanced"),
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
            plugin.namespacedKeyFactory.create("advanced"),
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
            plugin.namespacedKeyFactory.create(
                "advancement-shard"
            ), PersistentDataType.STRING
        )
            ?: return null
        return ArmorSets.getByID(shardSet)
    }
}

val Holder.armorSet: ArmorSet?
    get() = ArmorSets.getByID(this.id.key.removeSuffix("_advanced"))
