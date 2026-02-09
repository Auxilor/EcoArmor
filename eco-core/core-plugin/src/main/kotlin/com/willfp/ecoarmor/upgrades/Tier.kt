package com.willfp.ecoarmor.upgrades

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.recipe.Recipes
import com.willfp.eco.core.registry.Registrable
import com.willfp.eco.util.StringUtils
import com.willfp.ecoarmor.plugin
import com.willfp.ecoarmor.sets.ArmorSlot
import com.willfp.ecoarmor.sets.ArmorUtils.getCrystalTier
import com.willfp.libreforge.notNullMutableMapOf
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.Locale
import java.util.Objects

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
        for (slot in ArmorSlot.entries) {
            properties[slot] = TierProperties(
                this.config.getInt("properties." + slot.name.lowercase(Locale.getDefault()) + ".armor"),
                this.config.getInt("properties." + slot.name.lowercase(Locale.getDefault()) + ".toughness"),
                this.config
                    .getInt("properties." + slot.name.lowercase(Locale.getDefault()) + ".knockbackResistance"),
                this.config.getInt("properties." + slot.name.lowercase(Locale.getDefault()) + ".speedPercentage"),
                this.config
                    .getInt("properties." + slot.name.lowercase(Locale.getDefault()) + ".attackSpeedPercentage"),
                this.config
                    .getInt("properties." + slot.name.lowercase(Locale.getDefault()) + ".attackDamagePercentage"),
                this.config
                    .getInt("properties." + slot.name.lowercase(Locale.getDefault()) + ".attackKnockbackPercentage")
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
    val armor: Int,
    val toughness: Int,
    val knockback: Int,
    val speed: Int,
    val attackSpeed: Int,
    val attackDamage: Int,
    val attackKnockback: Int
)
