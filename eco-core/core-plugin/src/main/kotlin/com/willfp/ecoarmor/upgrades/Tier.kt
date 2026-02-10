package com.willfp.ecoarmor.upgrades

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.recipe.Recipes
import com.willfp.eco.core.recipe.recipes.ShapedCraftingRecipe
import com.willfp.eco.core.registry.Registrable
import com.willfp.eco.util.StringUtils
import com.willfp.ecoarmor.sets.ArmorSlot
import com.willfp.ecoarmor.sets.ArmorUtils.getCrystalTier
import com.willfp.libreforge.notNullMapOf
import com.willfp.libreforge.notNullMutableMapOf
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*


@Suppress("DEPRECATION")
class Tier(
    val id: String,
    private val config: Config,
    plugin: EcoPlugin
) : Registrable {
    /**
     * The display name of the crystal.
     */
    val displayName: String

    /**
     * The names of the tiers required for application.
     */
    private val requiredTiersForApplication: List<String>

    /**
     * If the crafting recipe is enabled.
     */
    val craftable: Boolean

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
        craftable = this.config.getBool("crystal.craftable")
        displayName = this.config.getFormattedString("display")
        requiredTiersForApplication = this.config.getStrings("requiresTiers")
        val key = plugin.namespacedKeyFactory.create("upgrade_crystal")
        val out = Items.lookup(this.config.getString("crystal.item")).item
        val outMeta = out.itemMeta!!
        val container = outMeta.persistentDataContainer
        container.set(key, PersistentDataType.STRING, id)
        @Suppress("UsePropertyAccessSyntax")
        outMeta.setDisplayName(this.config.getFormattedString("crystal.name"))
        val lore: MutableList<String> = ArrayList()
        for (loreLine in this.config.getStrings("crystal.lore")) {
            lore.add(Display.PREFIX + StringUtils.format(loreLine!!))
        }
        outMeta.lore = lore
        out.itemMeta = outMeta
        out.amount = 1 // who knows
        crystal = out
        for (slot in ArmorSlot.values()) {
            val path = "properties.${slot.name.lowercase(Locale.getDefault())}"

            properties[slot] = TierProperties(
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
        }

        CustomItem(
            plugin.namespacedKeyFactory.create("crystal_" + id.lowercase(Locale.getDefault())),
            { test: ItemStack? -> this == getCrystalTier(test!!) },
            out
        ).register()

        if (this.craftable) {
            val recipeOut = out.clone().apply {
                amount = config.getInt("crystal.giveAmount")
            }
            Recipes.createAndRegisterRecipe(
                plugin,
                "upgrade_crystal_$id",
                recipeOut,
                config.getStrings("crystal.recipe"),
                config.getStringOrNull("crystal.crafting-permission")
            )
        }
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

data class TierProperties(
    val armor: Int? = null,                           // flat armor points (e.g. 3)
    val toughness: Int? = null,                       // flat toughness (e.g. 2)
    val knockbackResistance: Int? = null,             // 0-100 for %
    val speedPercentage: Int? = null,                 // e.g. 10 = +10% speed
    val attackSpeedPercentage: Int? = null,           // e.g. 15 = +15% attack speed
    val attackDamagePercentage: Int? = null,          // e.g. 20 = +20% damage
    val attackKnockbackPercentage: Int? = null,       // e.g. 30 = +30% knockback
    val maxHealth: Int? = null,                       // flat extra hearts × 2 (e.g. 4 = +2 hearts)
    val attackDamageFlat: Int? = null,                // flat extra damage (e.g. 2 = +1 heart per hit)
    val attackSpeedFlat: Int? = null,                 // flat attack speed bonus
    val jumpStrength: Int? = null,                    // % jump height boost (e.g. 20 = +20%)
    val gravityPercentage: Int? = null,               // e.g. -30 = 30% less gravity (floatier), +50 = heavier
    val burningTimePercentage: Int? = null,           // e.g. -50 = 50% less burn time, +100 = double burn
    val explosionKnockbackResistance: Int? = null,    // 0-100 for %
    val oxygenBonus: Int? = null,                     // extra ticks of breath (e.g. 300 = +15 seconds)
    val movementEfficiency: Int? = null,              // 0-100 for % less slow in soul sand/honey/etc.
    val safeFallDistance: Int? = null,                // flat extra blocks safe to fall (e.g. 5 = safe from 8 blocks total)
    val entityInteractionRangePercentage: Int? = null,// e.g. 20 = +20% reach for attacking/interacting entities
    val blockInteractionRangePercentage: Int? = null  // e.g. 15 = +15% block reach
)