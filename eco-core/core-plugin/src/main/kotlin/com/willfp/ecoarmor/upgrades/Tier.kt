package com.willfp.ecoarmor.upgrades

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.config.toPlainValues
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.recipe.Recipes
import com.willfp.eco.core.recipe.recipes.CraftingRecipe
import com.willfp.eco.core.registry.Registrable
import com.willfp.eco.util.StringUtils
import com.willfp.ecoarmor.plugin
import com.willfp.ecoarmor.sets.ArmorSlot
import com.willfp.ecoarmor.sets.ArmorUtils.getCrystalTier
import com.willfp.libreforge.ConfigWarning
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.notNullMutableMapOf
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.Locale
import java.util.Objects

private const val ATTRIBUTE_MODIFIERS = "minecraft:attribute_modifiers"

@Suppress("DEPRECATION")
class Tier(
    val id: String,
    private val config: Config
) : Registrable {
    /**
     * The display name of the crystal.
     */
    val displayName: String = this.config.getFormattedString("display")

    /**
     * The names of the tiers required for application.
     */
    private val requiredTiersForApplication: List<String> = this.config.getStrings("requiresTiers")

    /**
     * If the crafting recipe is enabled.
     */
    val craftable: Boolean = this.config.getBool("crystal.craftable")

    /**
     * If applying this tier's crystal adds its modifiers on top of the item's
     * existing modifiers instead of replacing them.
     */
    val additive: Boolean = this.config.getBool("additive")

    /**
     * The max number of times this tier can be stacked onto the same item when
     * [additive] is true. -1 means unlimited.
     */
    val stackLimit: Int = this.config.getIntOrNull("stack-limit") ?: -1

    /**
     * The ItemStack of the crystal.
     */
    val crystal: ItemStack

    /**
     * Item properties.
     */
    val properties = notNullMutableMapOf<ArmorSlot, TierProperties>()

    /**
     * Create a new Tier.
     */
    init {
        val key = plugin.namespacedKeyFactory.create("upgrade_crystal")
        val out = Items.lookup(this.config.getString("crystal.item")).item
        val outMeta = out.itemMeta!!
        val container = outMeta.persistentDataContainer
        container.set(key, PersistentDataType.STRING, id)
        outMeta.setDisplayName(this.config.getFormattedString("crystal.name"))
        val lore: MutableList<String> = ArrayList()
        for (loreLine in this.config.getStrings("crystal.lore")) {
            lore.add(Display.PREFIX + StringUtils.format(loreLine!!))
        }
        outMeta.lore = lore
        out.itemMeta = outMeta
        out.amount = 1 // who knows
        crystal = out
        val context = ViolationContext(plugin, "Tier $id")
        val legacySlots = mutableListOf<String>()

        for (slot in ArmorSlot.entries) {
            val slotName = slot.name.lowercase(Locale.getDefault())
            val path = "properties.$slotName"

            properties[slot] = TierProperties(
                modifiers = readModifiers(slot, "$path.components", context),
                armor = config.getIntOrNull("$path.armor"),
                toughness = config.getIntOrNull("$path.toughness"),
                knockbackResistance = config.getIntOrNull("$path.knockbackResistance")
                    ?: config.getIntOrNull("$path.knockback"),
                speedPercentage = config.getIntOrNull("$path.speedPercentage")
                    ?: config.getIntOrNull("$path.speedPercentage"),
                attackSpeedPercentage = config.getIntOrNull("$path.attackSpeedPercentage"),
                attackDamagePercentage = config.getIntOrNull("$path.attackDamagePercentage"),
                attackKnockbackPercentage = config.getIntOrNull("$path.attackKnockbackPercentage"),
                maxHealth = config.getIntOrNull("$path.maxHealth"),
                attackDamageFlat = config.getIntOrNull("$path.attackDamageFlat"),
                attackSpeedFlat = config.getIntOrNull("$path.attackSpeedFlat"),
                jumpStrength = config.getIntOrNull("$path.jumpStrength"),
                gravityPercentage = config.getIntOrNull("$path.gravityPercentage"),
                burningTimePercentage = config.getIntOrNull("$path.burningTimePercentage"),
                explosionKnockbackResistance = config.getIntOrNull("$path.explosionKnockbackResistance"),
                oxygenBonus = config.getIntOrNull("$path.oxygenBonus"),
                movementEfficiency = config.getIntOrNull("$path.movementEfficiency"),
                safeFallDistance = config.getIntOrNull("$path.safeFallDistance"),
                entityInteractionRangePercentage = config.getIntOrNull("$path.entityReachPercentage")
                    ?: config.getIntOrNull("$path.entityInteractionRangePercentage"),
                blockInteractionRangePercentage = config.getIntOrNull("$path.blockReachPercentage")
                    ?: config.getIntOrNull("$path.blockInteractionRangePercentage")
            )

            if (properties[slot]?.hasLegacyStats == true) {
                legacySlots += slotName
            }
        }

        if (legacySlots.isNotEmpty()) {
            context.log(
                ConfigWarning(
                    "properties",
                    "The stat options on ${legacySlots.joinToString()} are deprecated and will be " +
                            "removed in a future version. Use a components section with " +
                            "attribute_modifiers instead."
                )
            )
        }

        CustomItem(
            plugin.namespacedKeyFactory.create("upgrade_crystal_" + id.lowercase(Locale.getDefault())),
            { test: ItemStack? -> test != null && this == getCrystalTier(test) },
            out
        ).register()

        val recipe: CraftingRecipe? = this.craftable
            .takeIf { it }
            ?.let {
                val recipeStrings = config.getStrings("crystal.recipe")
                if (recipeStrings.isEmpty()) return@let null

                val recipeOut = out.clone().apply {
                    amount = config.getInt("crystal.giveAmount")
                }

                Recipes.createAndRegisterRecipe(
                    plugin,
                    "upgrade_crystal_$id",
                    recipeOut,
                    recipeStrings,
                    config.getStringOrNull("crystal.crafting-permission"),
                    config.getBool("crystal.shapeless")
                )
            }
    }

    /**
     * Read the attribute modifiers a slot's components section configures.
     *
     * Only minecraft:attribute_modifiers is supported here: unlike the item's
     * own components, which are written once when the set is built, a tier's
     * modifiers are rewritten every time the tier changes, and only attribute
     * modifiers can be cleanly removed again.
     *
     * @param slot    The armor slot.
     * @param path    The config path of the components section.
     * @param context The context to log config problems to.
     * @return The modifiers.
     */
    private fun readModifiers(
        slot: ArmorSlot,
        path: String,
        context: ViolationContext
    ): List<TierModifier> {
        if (!config.has(path)) {
            return emptyList()
        }

        val components = config.getSubsection(path).toPlainValues()
            .mapKeys { (key, _) -> if (":" in key) key else "minecraft:$key" }

        for (key in components.keys) {
            if (key != ATTRIBUTE_MODIFIERS) {
                context.log(
                    ConfigWarning(
                        path,
                        "$key can't be set on a tier, only $ATTRIBUTE_MODIFIERS. " +
                                "Set it on the armor piece instead."
                    )
                )
            }
        }

        val configured = components[ATTRIBUTE_MODIFIERS] as? List<*> ?: return emptyList()

        return configured.mapNotNull { entry ->
            val modifier = entry as? Map<*, *>
                ?: return@mapNotNull warn(context, path, "a modifier is not a section")

            readModifier(slot, modifier, path, context)
        }
    }

    private fun readModifier(
        slot: ArmorSlot,
        modifier: Map<*, *>,
        path: String,
        context: ViolationContext
    ): TierModifier? {
        val type = modifier["type"]?.toString()
            ?: return warn(context, path, "a modifier is missing its type")

        val attribute = namespacedKey(type)?.let { Registry.ATTRIBUTE.get(it) }
            ?: return warn(context, path, "$type is not an attribute")

        val amount = (modifier["amount"] as? Number)?.toDouble()
            ?: return warn(context, path, "the $type modifier is missing its amount")

        val operation = operation(modifier["operation"]?.toString())
            ?: return warn(context, path, "${modifier["operation"]} is not an operation")

        val slotGroup = modifier["slot"]?.toString()?.let { EquipmentSlotGroup.getByName(it) }
            ?: slot.slotGroup

        // Tier modifiers are cleared and rewritten whenever the tier changes,
        // and clearing them means removing everything under this plugin's
        // namespace, so a tier can't put a modifier under anyone else's.
        val key = NamespacedKey(
            "ecoarmor",
            modifier["id"]?.toString()?.substringAfter(':')
                ?: "$id.${attribute.key.key}.${slot.name.lowercase(Locale.getDefault())}"
        )

        return TierModifier(attribute, key, amount, operation, slotGroup)
    }

    private fun namespacedKey(key: String) =
        if (":" in key) NamespacedKey.fromString(key) else NamespacedKey.minecraft(key)

    private fun operation(name: String?) = when (name?.lowercase(Locale.getDefault())) {
        null, "add_value" -> AttributeModifier.Operation.ADD_NUMBER
        "add_multiplied_base" -> AttributeModifier.Operation.ADD_SCALAR
        "add_multiplied_total" -> AttributeModifier.Operation.MULTIPLY_SCALAR_1
        else -> null
    }

    private fun warn(context: ViolationContext, path: String, message: String): TierModifier? {
        context.log(ConfigWarning(path, message))
        return null
    }

    /**
     * Get the required tiers for application.
     *
     * @return The tiers, or a blank list if always available.
     */
    fun getRequiredTiersForApplication(): List<Tier> {
        return requiredTiersForApplication.mapNotNull { Tiers.getByID(it) }
    }

    override fun getID(): String {
        return this.id
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Tier) {
            return false
        }
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(this.id)
    }
}

/**
 * An attribute modifier a tier applies to an armor piece, read from the tier's
 * components section.
 *
 * @param attribute The attribute the modifier applies to.
 * @param key       The modifier's key, always under the ecoarmor namespace.
 * @param amount    The modifier amount.
 * @param operation The modifier operation.
 * @param slot      The slot group the modifier applies in.
 */
data class TierModifier(
    val attribute: Attribute,
    val key: NamespacedKey,
    val amount: Double,
    val operation: AttributeModifier.Operation,
    val slot: EquipmentSlotGroup
)

/**
 * The stats a tier gives an armor piece in one slot.
 *
 * Every option other than [modifiers] is deprecated: they cover a fixed subset
 * of attributes with whole-number values, where a components section can
 * configure any attribute, with decimals, in any slot group.
 */
@Suppress("DEPRECATION")
data class TierProperties(
    /** The attribute modifiers configured through the components section. */
    val modifiers: List<TierModifier> = emptyList(),

    @Deprecated("Use a components section with attribute_modifiers instead")
    val armor: Int? = null,                           // flat armor points (e.g. 3)
    @Deprecated("Use a components section with attribute_modifiers instead")
    val toughness: Int? = null,                       // flat toughness (e.g. 2)
    @Deprecated("Use a components section with attribute_modifiers instead")
    val knockbackResistance: Int? = null,             // 0-100 for %
    @Deprecated("Use a components section with attribute_modifiers instead")
    val speedPercentage: Int? = null,                 // e.g. 10 = +10% speed
    @Deprecated("Use a components section with attribute_modifiers instead")
    val attackSpeedPercentage: Int? = null,           // e.g. 15 = +15% attack speed
    @Deprecated("Use a components section with attribute_modifiers instead")
    val attackDamagePercentage: Int? = null,          // e.g. 20 = +20% damage
    @Deprecated("Use a components section with attribute_modifiers instead")
    val attackKnockbackPercentage: Int? = null,       // e.g. 30 = +30% knockback
    @Deprecated("Use a components section with attribute_modifiers instead")
    val maxHealth: Int? = null,                       // flat extra hearts × 2 (e.g. 4 = +2 hearts)
    @Deprecated("Use a components section with attribute_modifiers instead")
    val attackDamageFlat: Int? = null,                // flat extra damage (e.g. 2 = +1 heart per hit)
    @Deprecated("Use a components section with attribute_modifiers instead")
    val attackSpeedFlat: Int? = null,                 // flat attack speed bonus
    @Deprecated("Use a components section with attribute_modifiers instead")
    val jumpStrength: Int? = null,                    // % jump height boost (e.g. 20 = +20%)
    @Deprecated("Use a components section with attribute_modifiers instead")
    val gravityPercentage: Int? = null,               // e.g. -30 = 30% less gravity (floatier), +50 = heavier
    @Deprecated("Use a components section with attribute_modifiers instead")
    val burningTimePercentage: Int? = null,           // e.g. -50 = 50% less burn time, +100 = double burn
    @Deprecated("Use a components section with attribute_modifiers instead")
    val explosionKnockbackResistance: Int? = null,    // 0-100 for %
    @Deprecated("Use a components section with attribute_modifiers instead")
    val oxygenBonus: Int? = null,                     // extra ticks of breath (e.g. 300 = +15 seconds)
    @Deprecated("Use a components section with attribute_modifiers instead")
    val movementEfficiency: Int? = null,              // 0-100 for % less slow in soul sand/honey/etc.
    @Deprecated("Use a components section with attribute_modifiers instead")
    val safeFallDistance: Int? = null,                // flat extra blocks safe to fall (e.g. 5 = safe from 8 blocks total)
    @Deprecated("Use a components section with attribute_modifiers instead")
    val entityInteractionRangePercentage: Int? = null,// e.g. 20 = +20% reach for attacking/interacting entities
    @Deprecated("Use a components section with attribute_modifiers instead")
    val blockInteractionRangePercentage: Int? = null  // e.g. 15 = +15% block reach
) {
    /** If any of the deprecated stat options are set. */
    val hasLegacyStats: Boolean
        get() = listOf(
            armor, toughness, knockbackResistance, speedPercentage, attackSpeedPercentage,
            attackDamagePercentage, attackKnockbackPercentage, maxHealth, attackDamageFlat,
            attackSpeedFlat, jumpStrength, gravityPercentage, burningTimePercentage,
            explosionKnockbackResistance, oxygenBonus, movementEfficiency, safeFallDistance,
            entityInteractionRangePercentage, blockInteractionRangePercentage
        ).any { it != null }
}
