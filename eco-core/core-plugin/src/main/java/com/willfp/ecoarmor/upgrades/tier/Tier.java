package com.willfp.ecoarmor.upgrades.tier;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.willfp.eco.internal.config.AbstractUndefinedConfig;
import com.willfp.eco.util.StringUtils;
import com.willfp.eco.util.config.updating.annotations.ConfigUpdater;
import com.willfp.eco.util.display.Display;
import com.willfp.eco.util.recipe.RecipeParts;
import com.willfp.eco.util.recipe.parts.ComplexRecipePart;
import com.willfp.eco.util.recipe.recipes.EcoShapedRecipe;
import com.willfp.ecoarmor.EcoArmorPlugin;
import com.willfp.ecoarmor.config.BaseTierConfig;
import com.willfp.ecoarmor.config.CustomConfig;
import com.willfp.ecoarmor.sets.meta.ArmorSlot;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Tier {
    /**
     * Registered crystals.
     */
    private static final BiMap<String, Tier> BY_NAME = HashBiMap.create();

    /**
     * Sets that exist by default.
     */
    private static final List<String> DEFAULT_TIER_NAMES = Arrays.asList(
            "default",
            "iron",
            "diamond",
            "netherite",
            "manyullyn",
            "cobalt",
            "osmium",
            "exotic"
    );

    /**
     * Instance of EcoArmor to create keys for.
     */
    @Getter
    private final EcoArmorPlugin plugin = EcoArmorPlugin.getInstance();

    /**
     * The tier name.
     */
    @Getter
    private final String name;

    /**
     * The config of the crystal.
     */
    @Getter
    private final AbstractUndefinedConfig config;

    /**
     * The display name of the crystal.
     */
    @Getter
    private String displayName;

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
     */
    public Tier(@NotNull final String tierName,
                @NotNull final AbstractUndefinedConfig config) {
        this.name = tierName;
        this.config = config;
        if (!this.config.getBool("enabled") && !this.getName().equalsIgnoreCase("default")) {
            return;
        }

        BY_NAME.put(tierName, this);
        this.update();
    }

    /**
     * Update the tracker's crafting recipe.
     */
    public void update() {
        this.enabled = this.getConfig().getBool("crystal-craftable");
        this.displayName = this.getConfig().getString("display");
        NamespacedKey key = this.getPlugin().getNamespacedKeyFactory().create("upgrade_crystal");

        ItemStack out = new ItemStack(Material.END_CRYSTAL);
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
            EcoShapedRecipe.Builder builder = EcoShapedRecipe.builder(this.getPlugin(), "upgrade_crystal_" + name)
                    .setOutput(out);

            List<String> recipeStrings = this.getConfig().getStrings("crystal-recipe");

            RecipeParts.registerRecipePart(this.getPlugin().getNamespacedKeyFactory().create("upgrade_crystal_" + name), new ComplexRecipePart(test -> {
                if (ArmorUtils.getCrystalTier(test) == null) {
                    return false;
                }
                return Objects.equals(name, ArmorUtils.getCrystalTier(test));
            }, out));

            for (int i = 0; i < 9; i++) {
                builder.setRecipePart(i, RecipeParts.lookup(recipeStrings.get(i)));
            }

            this.crystalRecipe = builder.build();
            this.crystalRecipe.register();
        }
    }

    /**
     * Get {@link Tier} matching name.
     *
     * @param name The name to search for.
     * @return The matching {@link Tier}, or null if not found.
     */
    public static Tier getByName(@Nullable final String name) {
        return BY_NAME.get(name);
    }

    /**
     * Get all registered {@link Tier}s.
     *
     * @return A list of all {@link Tier}s.
     */
    public static List<Tier> values() {
        return ImmutableList.copyOf(BY_NAME.values());
    }

    /**
     * Update.
     */
    @ConfigUpdater
    public static void reload() {
        BY_NAME.clear();

        for (String defaultSetName : DEFAULT_TIER_NAMES) {
            new Tier(defaultSetName, new BaseTierConfig(defaultSetName));
        }

        try {
            Files.walk(Paths.get(new File(EcoArmorPlugin.getInstance().getDataFolder(), "tiers/").toURI()))
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        String name = path.getFileName().toString().replace(".yml", "");
                        new Tier(
                                name,
                                new CustomConfig(name, YamlConfiguration.loadConfiguration(path.toFile()))
                        );
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
