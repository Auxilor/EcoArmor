package com.willfp.ecoarmor.upgrades.crystal;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.willfp.eco.util.StringUtils;
import com.willfp.eco.util.config.updating.annotations.ConfigUpdater;
import com.willfp.eco.util.recipe.EcoShapedRecipe;
import com.willfp.eco.util.recipe.lookup.RecipePartUtils;
import com.willfp.eco.util.recipe.parts.ComplexRecipePart;
import com.willfp.ecoarmor.EcoArmorPlugin;
import com.willfp.ecoarmor.config.EcoArmorConfigs;
import com.willfp.ecoarmor.display.ArmorDisplay;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
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
import java.util.List;
import java.util.Objects;

public class UpgradeCrystal {
    /**
     * Registered crystals.
     */
    private static final BiMap<String, UpgradeCrystal> BY_NAME = HashBiMap.create();

    /**
     * Instance of EcoArmor to create keys for.
     */
    @Getter
    private final EcoArmorPlugin plugin = EcoArmorPlugin.getInstance();

    /**
     * The tier of the crystal.
     */
    @Getter
    private final String tier;

    /**
     * If the crafting recipe is enabled.
     */
    @Getter
    private boolean enabled;

    /**
     * The ItemStack of the crystal.
     */
    @Getter
    private ItemStack itemStack;

    /**
     * The crafting recipe to make the crystal.
     */
    @Getter
    private EcoShapedRecipe recipe;

    /**
     * Create a new Upgrade Crystal.
     *
     * @param tier The tier to upgrade to.
     */
    public UpgradeCrystal(@NotNull final String tier) {
        this.tier = tier;
        BY_NAME.put(tier, this);
        this.update();
    }

    /**
     * Update the tracker's crafting recipe.
     */
    public void update() {
        this.enabled = EcoArmorConfigs.TIERS.getBool(tier + ".crystal-craftable");
        NamespacedKey key = this.getPlugin().getNamespacedKeyFactory().create("upgrade_crystal");

        ItemStack out = new ItemStack(Material.END_CRYSTAL);
        ItemMeta outMeta = out.getItemMeta();
        assert outMeta != null;
        PersistentDataContainer container = outMeta.getPersistentDataContainer();
        container.set(key, PersistentDataType.STRING, tier);

        outMeta.setDisplayName(EcoArmorConfigs.TIERS.getString(tier + ".crystal-name"));

        List<String> lore = new ArrayList<>();
        for (String loreLine : EcoArmorConfigs.TIERS.getStrings(tier + ".crystal-lore")) {
            lore.add(ArmorDisplay.PREFIX + StringUtils.translate(loreLine));
        }
        outMeta.setLore(lore);

        out.setItemMeta(outMeta);
        this.itemStack = out;

        if (this.isEnabled()) {
            EcoShapedRecipe.Builder builder = EcoShapedRecipe.builder(this.getPlugin(), "upgrade_crystal_" + tier)
                    .setOutput(out);

            List<String> recipeStrings = EcoArmorConfigs.TIERS.getStrings(tier + ".crystal-recipe");

            RecipePartUtils.registerLookup("ecoarmor:upgrade_crystal_" + tier, s -> new ComplexRecipePart(test -> Objects.equals(tier, ArmorUtils.getCrystalTier(test)), out));

            for (int i = 0; i < 9; i++) {
                builder.setRecipePart(i, RecipePartUtils.lookup(recipeStrings.get(i)));
            }

            this.recipe = builder.build();
            this.recipe.register();
        }
    }

    /**
     * Get {@link UpgradeCrystal} matching name.
     *
     * @param name The name to search for.
     * @return The matching {@link UpgradeCrystal}, or null if not found.
     */
    public static UpgradeCrystal getByName(@Nullable final String name) {
        return BY_NAME.get(name);
    }

    /**
     * Get all registered {@link UpgradeCrystal}s.
     *
     * @return A list of all {@link UpgradeCrystal}s.
     */
    public static List<UpgradeCrystal> values() {
        return ImmutableList.copyOf(BY_NAME.values());
    }

    /**
     * Update.
     */
    @ConfigUpdater
    public static void reload() {
        BY_NAME.clear();

        for (String key : EcoArmorConfigs.TIERS.getConfig().getKeys(false)) {
            new UpgradeCrystal(key);
        }

        BY_NAME.values().forEach(UpgradeCrystal::update);
    }
}
