package com.willfp.ecoarmor.upgrades;

import com.willfp.eco.util.StringUtils;
import com.willfp.eco.util.config.Config;
import com.willfp.eco.util.display.Display;
import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.eco.util.recipe.RecipeParts;
import com.willfp.eco.util.recipe.parts.ComplexRecipePart;
import com.willfp.eco.util.recipe.recipes.EcoShapedRecipe;
import com.willfp.ecoarmor.sets.meta.ArmorSlot;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Tier extends PluginDependent {
    /**
     * The tier name.
     */
    @Getter
    private final String name;

    /**
     * The config of the crystal.
     */
    @Getter(AccessLevel.PRIVATE)
    private final Config config;

    /**
     * The display name of the crystal.
     */
    @Getter
    private String displayName;

    /**
     * The names of the tiers required for application.
     */
    private List<String> requiredTiersForApplication;

    /**
     * If the crafting recipe is enabled.
     */
    @Getter
    private boolean enabled;

    /**
     * The ItemStack of the crystal.
     */
    @Getter
    private ItemStack crystal;

    /**
     * The crafting recipe to make the crystal.
     */
    @Getter
    private EcoShapedRecipe crystalRecipe;

    /**
     * Item properties.
     */
    @Getter
    private final Map<ArmorSlot, TierProperties> properties = new HashMap<>();

    /**
     * Create a new Tier.
     *
     * @param tierName The name of the tier.
     * @param config   The config of the tier.
     * @param plugin   Instance of EcoArmor.
     */
    public Tier(@NotNull final String tierName,
                @NotNull final Config config,
                @NotNull final AbstractEcoPlugin plugin) {
        super(plugin);
        this.name = tierName;
        this.config = config;
        if (!this.config.getBool("enabled") && !this.getName().equalsIgnoreCase("default")) {
            return;
        }

        Tiers.addNewTier(this);
        this.update();
    }

    /**
     * Update the tracker's crafting recipe.
     */
    public void update() {
        this.enabled = this.getConfig().getBool("crystal-craftable");
        this.displayName = this.getConfig().getString("display");
        this.requiredTiersForApplication = this.getConfig().getStrings("requires-tiers");
        NamespacedKey key = this.getPlugin().getNamespacedKeyFactory().create("upgrade_crystal");

        ItemStack out = new ItemStack(Objects.requireNonNull(Material.getMaterial(this.getPlugin().getConfigYml().getString("upgrade-crystal-material").toUpperCase())));
        ItemMeta outMeta = out.getItemMeta();
        assert outMeta != null;
        PersistentDataContainer container = outMeta.getPersistentDataContainer();
        container.set(key, PersistentDataType.STRING, name);

        outMeta.setDisplayName(this.getConfig().getString("crystal-name"));

        List<String> lore = new ArrayList<>();
        for (String loreLine : this.getConfig().getStrings("crystal-lore")) {
            lore.add(Display.PREFIX + StringUtils.translate(loreLine));
        }
        outMeta.setLore(lore);

        out.setItemMeta(outMeta);
        out.setAmount(1); // who knows
        this.crystal = out;

        for (ArmorSlot slot : ArmorSlot.values()) {
            properties.put(slot, new TierProperties(
                    this.getConfig().getInt("properties." + slot.name().toLowerCase() + ".armor"),
                    this.getConfig().getInt("properties." + slot.name().toLowerCase() + ".toughness"),
                    this.getConfig().getInt("properties." + slot.name().toLowerCase() + ".knockback-resistance"),
                    this.getConfig().getInt("properties." + slot.name().toLowerCase() + ".speed-percentage"),
                    this.getConfig().getInt("properties." + slot.name().toLowerCase() + ".attack-speed-percentage"),
                    this.getConfig().getInt("properties." + slot.name().toLowerCase() + ".attack-damage-percentage"),
                    this.getConfig().getInt("properties." + slot.name().toLowerCase() + ".attack-knockback-percentage")
            ));
        }

        if (this.isEnabled()) {
            ItemStack recipeOut = out.clone();
            recipeOut.setAmount(this.getConfig().getInt("recipe-give-amount"));
            EcoShapedRecipe.Builder builder = EcoShapedRecipe.builder(this.getPlugin(), "upgrade_crystal_" + name)
                    .setOutput(recipeOut);

            List<String> recipeStrings = this.getConfig().getStrings("crystal-recipe");

            RecipeParts.registerRecipePart(this.getPlugin().getNamespacedKeyFactory().create("upgrade_crystal_" + name), new ComplexRecipePart(test -> {
                if (test == null) {
                    return false;
                }
                if (ArmorUtils.getCrystalTier(test) == null) {
                    return false;
                }
                return this.equals(ArmorUtils.getCrystalTier(test));
            }, out));

            for (int i = 0; i < 9; i++) {
                builder.setRecipePart(i, RecipeParts.lookup(recipeStrings.get(i)));
            }

            this.crystalRecipe = builder.build();
            this.crystalRecipe.register();
        }
    }

    /**
     * Get the required tiers for application.
     *
     * @return The tiers, or a blank list if always available.
     */
    public List<Tier> getRequiredTiersForApplication() {
        return requiredTiersForApplication.stream().map(Tiers::getByName).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tier)) {
            return false;
        }

        Tier tier = (Tier) o;
        return Objects.equals(getName(), tier.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
