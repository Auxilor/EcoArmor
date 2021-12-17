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
import com.willfp.libreforge.Holder
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.Effects
import org.bukkit.Bukkit
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
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
    private val items: MutableMap<ArmorSlot, ItemStack> = EnumMap(ArmorSlot::class.java)

    /**
     * Items in advanced set.
     */
    private val advancedItems: MutableMap<ArmorSlot, ItemStack> = EnumMap(ArmorSlot::class.java)

    /**
     * Advancement shard item.
     */
    val advancementShardItem: ItemStack

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
        regularHolder = RegularHolder(conditions, effects)
        advancedHolder = AdvancedHolder(conditions, advancedEffects)
        ArmorSets.addNewSet(this)
        for (slot in ArmorSlot.values()) {
            val item = construct(slot, config.getSubsection(slot.name.lowercase(Locale.getDefault())), false)
            items[slot] = item
            constructRecipe(slot, config.getSubsection(slot.name.lowercase(Locale.getDefault())), item)
            val advancedItem = construct(slot, config.getSubsection(slot.name.lowercase(Locale.getDefault())), true)
            advancedItems[slot] = advancedItem
        }
        advancementShardItem = constructShard()
    }

    private fun constructShard(): ItemStack {
        val shardLore = config.getStrings("advancementShardLore")
        shardLore.replaceAll { Display.PREFIX + it }
        val shard = ItemStackBuilder(
            Items.lookup(
                plugin.configYml.getString("advancement-shard-material").lowercase(Locale.getDefault())
            ).item
        )
            .setDisplayName(config.getString("advancementShardName"))
            .addEnchantment(Enchantment.DURABILITY, 3)
            .addItemFlag(ItemFlag.HIDE_ENCHANTS)
            .addLoreLines(shardLore)
            .writeMetaKey(plugin.namespacedKeyFactory.create("advancement-shard"), PersistentDataType.STRING, id)
            .build()
        if (config.getBool("shardCraftable")) {
            Recipes.createAndRegisterRecipe(
                plugin,
                id + "_shard",
                shard,
                config.getStrings("shardRecipe")
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
        val builder: ItemBuilder = ItemStackBuilder(Items.lookup(slotConfig.getString("item")).item)
            .setDisplayName(if (advanced) slotConfig.getString("advancedName") else slotConfig.getString("name"))
            .addLoreLines(slotConfig.getStrings("lore").stream().map { s: String -> Display.PREFIX + s }
                .collect(Collectors.toList()))
            .addLoreLines {
                if (advanced) {
                    return@addLoreLines config.getStrings("advancedLore").stream()
                        .map { s: String -> Display.PREFIX + s }
                        .collect(Collectors.toList())
                } else {
                    return@addLoreLines null
                }
            }
            .setDisplayName { if (advanced) slotConfig.getString("advancedName") else slotConfig.getString("name") }
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
                    if (!isAdvanced(itemStack)) {
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
                    if (isAdvanced(itemStack)) {
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
            Recipes.createAndRegisterRecipe(
                plugin,
                id + "_" + slot.name.lowercase(Locale.getDefault()),
                formattedOut,
                slotConfig.getStrings("recipe")
            )
        }
    }

    /**
     * Get item stack from slot.
     *
     * @param slot The slot.
     * @return The item.
     */
    fun getItemStack(slot: ArmorSlot): ItemStack {
        return items[slot]!!
    }

    /**
     * Get item stack from slot.
     *
     * @param slot The slot.
     * @return The item.
     */
    fun getAdvancedItemStack(slot: ArmorSlot): ItemStack {
        return advancedItems[slot]!!
    }

    /**
     * Get default tier for slot.
     *
     * @param slot The slot.
     * @return The tier.
     */
    fun getDefaultTier(slot: ArmorSlot?): Tier {
        if (slot == null) return Tiers.defaultTier
        val tier = Tiers.getByID(config.getSubsection(slot.name.lowercase()).getString("defaultTier"))
        return tier ?: Tiers.defaultTier
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is ArmorSet) {
            return false
        }

        return id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun toString(): String {
        return ("ArmorSet{"
                + id
                + "}")
    }
}