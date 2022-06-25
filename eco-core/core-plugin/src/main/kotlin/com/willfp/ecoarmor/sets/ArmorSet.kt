package com.willfp.ecoarmor.sets

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.ItemBuilder
import com.willfp.eco.core.items.builder.ItemStackBuilder
import com.willfp.eco.core.recipe.Recipes
import com.willfp.ecoarmor.sets.ArmorSlot.Companion.getSlot
import com.willfp.ecoarmor.sets.ArmorUtils.getSetOnItem
import com.willfp.ecoarmor.sets.ArmorUtils.getShardSet
import com.willfp.ecoarmor.sets.ArmorUtils.isAdvanced
import com.willfp.ecoarmor.sets.ArmorUtils.setAdvanced
import com.willfp.ecoarmor.sets.ArmorUtils.setTier
import com.willfp.ecoarmor.upgrades.Tier
import com.willfp.ecoarmor.upgrades.Tiers
import com.willfp.ecoarmor.util.PlayableSound
import com.willfp.ecoarmor.util.notNullMapOf
import com.willfp.libreforge.Holder
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.Effects
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*
import java.util.stream.Collectors

class ArmorSet(
    private val config: Config,
    private val plugin: EcoPlugin
) {
    /**
     * The name of the set.
     */
    val id: String = config.getString("id")

    /**
     * The advanced holder.
     */
    val advancedHolder: Holder

    /**
     * The regular holder.
     */
    val regularHolder: Holder

    /**
     * Items in set.
     */
    private val items = notNullMapOf<ArmorSlot, ItemStack>()

    /**
     * Holders in set.
     */
    private val slotHolders = notNullMapOf<ArmorSlot, Holder>()

    /**
     * Items in advanced set.
     */
    private val advancedItems = notNullMapOf<ArmorSlot, ItemStack>()

    /**
     * Holders in advanced set.
     */
    private val advancedSlotHolders = notNullMapOf<ArmorSlot, Holder>()

    /**
     * Advancement shard item.
     */
    val advancementShardItem: ItemStack

    /*
    * Equip Sound
     */
    val equipSound = if (config.getBool("sounds.equip.enabled")) {
        PlayableSound(
            Sound.valueOf(config.getString("sounds.equip.sound").uppercase()),
            config.getDouble("sounds.equip.volume"),
            config.getDouble("sounds.equip.pitch")
        )
    } else null

    /*
    * Advanced equip Sound
     */
    val advancedEquipSound = if (config.getBool("sounds.advancedEquip.enabled")) {
        PlayableSound(
            Sound.valueOf(config.getString("sounds.advancedEquip.sound").uppercase()),
            config.getDouble("sounds.advancedEquip.volume"),
            config.getDouble("sounds.advancedEquip.pitch")
        )
    } else null

    /*
    * Unequip Sound
     */
    val unequipSound = if (config.getBool("sounds.unequip.enabled")) {
        PlayableSound(
            Sound.valueOf(config.getString("sounds.unequip.sound").uppercase()),
            config.getDouble("sounds.unequip.volume"),
            config.getDouble("sounds.unequip.pitch")
        )
    } else null

    /**
     * Create a new Armor Set.
     */
    init {
        val conditions: MutableSet<ConfiguredCondition> = HashSet()
        for (cfg in config.getSubsections("conditions")) {
            val conf = Conditions.compile(cfg, "Armor Set $id")
            if (conf != null) {
                conditions.add(conf)
            }
        }
        val effects: MutableSet<ConfiguredEffect> = HashSet()
        for (cfg in config.getSubsections("effects")) {
            val conf = Effects.compile(cfg, "Armor Set $id")
            if (conf != null) {
                effects.add(conf)
            }
        }
        val advancedEffects: MutableSet<ConfiguredEffect> = HashSet()
        for (cfg in config.getSubsections("advancedEffects")) {
            val conf = Effects.compile(cfg, "Armor Set $id (Advanced)")
            if (conf != null) {
                advancedEffects.add(conf)
            }
        }
        regularHolder = SimpleHolder(conditions, effects, id)
        advancedHolder = SimpleHolder(conditions, advancedEffects, "${id}_advanced")
        ArmorSets.addNewSet(this)
        for (slot in ArmorSlot.values()) {
            val slotConfig = config.getSubsection(slot.name.lowercase(Locale.getDefault()))
            val item = construct(slot, slotConfig, false)
            items[slot] = item
            constructRecipe(slot, slotConfig, item)
            val advancedItem = construct(slot, slotConfig, true)
            advancedItems[slot] = advancedItem

            slotHolders[slot] = SimpleHolder(
                slotConfig.getSubsections("conditions").mapNotNull {
                    Conditions.compile(it, "Armor Set $id - $slot")
                }.toSet(),
                slotConfig.getSubsections("effects").mapNotNull {
                    Effects.compile(it, "Armor Set $id - $slot")
                }.toSet(),
                "${id}_${slot.name.lowercase()}"
            )

            advancedSlotHolders[slot] = SimpleHolder(
                slotConfig.getSubsections("conditions").mapNotNull {
                    Conditions.compile(it, "Armor Set $id - $slot")
                }.toSet(),
                slotConfig.getSubsections("advancedEffects").mapNotNull {
                    Effects.compile(it, "Armor Set $id - $slot (Advanced)")
                }.toSet(),
                "${id}_${slot.name.lowercase()}_advanced"
            )
        }
        advancementShardItem = constructShard()
    }

    private fun constructShard(): ItemStack {
        val shardLore = config.getStrings("shard.lore")
        shardLore.replaceAll { Display.PREFIX + it }
        val shard = ItemStackBuilder(
            Items.lookup(config.getString("shard.item"))
        )
            .setDisplayName(config.getFormattedString("shard.name"))
            .addLoreLines(shardLore)
            .writeMetaKey(plugin.namespacedKeyFactory.create("advancement-shard"), PersistentDataType.STRING, id)
            .build()
        if (config.getBool("shard.craftable")) {
            Recipes.createAndRegisterRecipe(
                plugin,
                id + "_shard",
                shard,
                config.getStrings("shard.recipe")
            )
        }
        CustomItem(
            plugin.namespacedKeyFactory.create("shard_" + id.lowercase(Locale.getDefault())),
            { test: ItemStack? -> this == getShardSet(test!!) },
            shard
        ).register()
        return shard
    }

    private fun construct(
        slot: ArmorSlot,
        slotConfig: Config,
        advanced: Boolean
    ): ItemStack {
        val builder: ItemBuilder = ItemStackBuilder(Items.lookup(slotConfig.getString("item")).item).apply {
            setDisplayName(
                if (advanced) slotConfig.getFormattedString("advancedName") else slotConfig.getFormattedString(
                    "name"
                )
            )
            val defaultLore = slotConfig.getFormattedStrings("lore").stream().map { s: String -> Display.PREFIX + s }
                .collect(Collectors.toList())
            val advancedLore = config.getFormattedStrings("advancedLore").stream()
                .map { s: String -> Display.PREFIX + s }
                .collect(Collectors.toList())

            if (advanced) {
                if (!plugin.configYml.getBool("advanced-lore-only")) {
                    addLoreLines(defaultLore)
                }
                addLoreLines(advancedLore)
            } else {
                addLoreLines(defaultLore)
            }

            setDisplayName {
                if (advanced) slotConfig.getFormattedString("advancedName") else slotConfig.getFormattedString(
                    "name"
                )
            }
        }
        builder.writeMetaKey(
            plugin.namespacedKeyFactory.create("set"),
            PersistentDataType.STRING,
            id
        ).writeMetaKey(
            plugin.namespacedKeyFactory.create("effective-durability"),
            PersistentDataType.INTEGER,
            slotConfig.getInt("effectiveDurability")
        )
        val itemStack = builder.build()
        setAdvanced(itemStack, advanced)
        val defaultTier = Tiers.getByID(slotConfig.getString("defaultTier"))
        if (defaultTier == null) {
            Bukkit.getLogger()
                .warning("Default tier specified in " + id + " " + slot.name.lowercase(Locale.getDefault()) + " is invalid! Defaulting to 'default'")
            setTier(itemStack, Tiers.defaultTier)
        } else {
            setTier(itemStack, defaultTier)
        }
        if (advanced) {
            CustomItem(
                plugin.namespacedKeyFactory.create(
                    "set_" + id.lowercase(Locale.getDefault()) + "_" + slot.name.lowercase(
                        Locale.getDefault()
                    ) + "_advanced"
                ), { test ->
                    if (getSlot(test) !== getSlot(itemStack)) {
                        return@CustomItem false
                    }
                    if (!isAdvanced(test)) {
                        return@CustomItem false
                    }
                    if (getSetOnItem(test) == null) {
                        return@CustomItem false
                    }
                    this == getSetOnItem(test)
                }, itemStack
            ).register()
        } else {
            CustomItem(
                plugin.namespacedKeyFactory.create(
                    "set_" + id.lowercase(Locale.getDefault()) + "_" + slot.name.lowercase(
                        Locale.getDefault()
                    )
                ),
                { test ->
                    if (getSlot(test) !== getSlot(itemStack)) {
                        return@CustomItem false
                    }
                    if (isAdvanced(test)) {
                        return@CustomItem false
                    }
                    if (getSetOnItem(test) == null) {
                        return@CustomItem false
                    }
                    this == getSetOnItem(test)
                }, itemStack
            ).register()
        }
        return itemStack
    }

    private fun constructRecipe(
        slot: ArmorSlot,
        slotConfig: Config,
        out: ItemStack
    ) {
        if (slotConfig.getBool("craftable")) {
            val formattedOut = out.clone()
            val meta = formattedOut.itemMeta ?: return
            val metaLore = meta.lore ?: emptyList()
            val lore = metaLore.map { it.replace("%tier%", Tiers.defaultTier.displayName) }
            meta.lore = lore
            formattedOut.itemMeta = meta
            plugin.scheduler.run {
                Recipes.createAndRegisterRecipe(
                    plugin,
                    id + "_" + slot.name.lowercase(Locale.getDefault()),
                    formattedOut,
                    slotConfig.getStrings("recipe")
                )
            }
        }
    }

    /**
     * Get item stack from slot.
     *
     * @param slot The slot.
     * @return The item.
     */
    fun getItemStack(slot: ArmorSlot): ItemStack {
        return items[slot]
    }

    /**
     * Get item stack from slot.
     *
     * @param slot The slot.
     * @return The item.
     */
    fun getAdvancedItemStack(slot: ArmorSlot): ItemStack {
        return advancedItems[slot]
    }

    /**
     * Get default tier for slot.
     *
     * @param slot The slot.
     * @return The tier.
     */
    fun getDefaultTier(slot: ArmorSlot?): Tier {
        slot ?: return Tiers.defaultTier
        val tier = Tiers.getByID(config.getSubsection(slot.name.lowercase()).getString("defaultTier"))
        return tier ?: Tiers.defaultTier
    }

    fun getSpecificHolder(itemStack: ItemStack): Holder? {
        val slot = getSlot(itemStack) ?: return null
        val advanced = isAdvanced(itemStack)

        return if (advanced) {
            advancedSlotHolders[slot]
        } else {
            slotHolders[slot]
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ArmorSet) {
            return false
        }

        return id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun toString(): String {
        return "ArmorSet{$id}"
    }
}

class SimpleHolder(
    override val conditions: Set<ConfiguredCondition>,
    override val effects: Set<ConfiguredEffect>,
    override val id: String
) : Holder
