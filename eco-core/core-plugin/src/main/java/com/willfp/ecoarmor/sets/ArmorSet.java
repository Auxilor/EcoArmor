package com.willfp.ecoarmor.sets;

import com.willfp.eco.common.recipes.lookup.RecipePartUtils;
import com.willfp.eco.util.ProxyUtils;
import com.willfp.eco.util.StringUtils;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.eco.util.recipe.EcoShapedRecipe;
import com.willfp.ecoarmor.config.EcoArmorConfigs;
import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.effects.Effects;
import com.willfp.ecoarmor.proxy.proxies.SkullProxy;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ArmorSet {
    /**
     * Instance of EcoArmor.
     */
    private static final AbstractEcoPlugin PLUGIN = AbstractEcoPlugin.getInstance();

    /**
     * The name of the set.
     */
    @Getter
    private final String name;

    /**
     * Effects and their strengths.
     */
    @Getter
    private final Map<Effect, Double> effects = new HashMap<>();

    /**
     * Potion effects to be applied on equip.
     */
    @Getter
    private final Map<PotionEffectType, Integer> potionEffects = new HashMap<>();

    /**
     * Helmet ItemStack.
     */
    @Getter
    private final ItemStack helmet;

    /**
     * Chestplate ItemStack.
     */
    @Getter
    private final ItemStack chestplate;

    /**
     * Leggings ItemStack.
     */
    @Getter
    private final ItemStack leggings;

    /**
     * Boots ItemStack.
     */
    @Getter
    private final ItemStack boots;

    /**
     * Create a new Armor Set.
     *
     * @param name The name of the set.
     */
    public ArmorSet(@NotNull final String name) {
        this.name = name;

        for (String effectName : EcoArmorConfigs.SETS.getConfig().getConfigurationSection(name + ".set-bonus").getKeys(false)) {
            Effect effect = Effects.getByName(effectName);
            double value = EcoArmorConfigs.SETS.getDouble(name + ".set-bonus." + effectName);
            effects.put(effect, value);
        }

        helmet = construct("helmet");
        chestplate = construct("chestplate");
        leggings = construct("leggings");
        boots = construct("boots");

        ArmorSets.addNewSet(this);
    }

    private ItemStack construct(@NotNull final String slot) {
        String pieceName = slot.toLowerCase();

        Material material = Material.getMaterial(EcoArmorConfigs.SETS.getString(name + "." + pieceName + ".material").toUpperCase());
        String displayName = EcoArmorConfigs.SETS.getString(name + "." + pieceName + ".name");
        Map<Enchantment, Integer> enchants = new HashMap<>();
        for (String enchantKey : EcoArmorConfigs.SETS.getConfig().getConfigurationSection(name + "." + pieceName + ".enchants").getKeys(false)) {
            int level = EcoArmorConfigs.SETS.getInt(name + "." + pieceName + ".enchants." + enchantKey);
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantKey));
            enchants.put(enchantment, level);
        }

        List<String> lore = new ArrayList<>();
        for (String loreLine : EcoArmorConfigs.SETS.getStrings(name + "." + pieceName + ".lore")) {
            lore.add(StringUtils.translate(loreLine));
        }

        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();

        assert meta != null;

        if (material == Material.PLAYER_HEAD) {
            String base64 = EcoArmorConfigs.SETS.getString(name + "." + pieceName + ".skull-texture");

            ProxyUtils.getProxy(SkullProxy.class).setTalismanTexture((SkullMeta) meta, base64);
        }

        if (material.toString().toLowerCase().contains("leather")) {
            String colorString = EcoArmorConfigs.SETS.getString(name + "." + pieceName + ".leather-color");

            java.awt.Color awtColor = java.awt.Color.decode(colorString);

            ((LeatherArmorMeta) meta).setColor(Color.fromRGB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue()));
            meta.addItemFlags(ItemFlag.HIDE_DYE);
        }

        meta.setDisplayName(displayName);
        enchants.forEach((enchantment, integer) -> meta.addEnchant(enchantment, integer, true));
        meta.setLore(lore);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(PLUGIN.getNamespacedKeyFactory().create("set"), PersistentDataType.STRING, name);
        itemStack.setItemMeta(meta);

        constructRecipe(slot, itemStack);

        return itemStack;
    }

    private void constructRecipe(@NotNull final String slot,
                                 @NotNull final ItemStack out) {
        EcoShapedRecipe.Builder builder = EcoShapedRecipe.builder(PLUGIN, this.getName() + "_" + slot).setOutput(out);

        List<String> recipeStrings = EcoArmorConfigs.SETS.getStrings(name + "." + slot.toLowerCase() + ".recipe");

        for (int i = 0; i < 9; i++) {
            builder.setRecipePart(i, RecipePartUtils.lookup(recipeStrings.get(i)));
        }

        EcoShapedRecipe recipe = builder.build();
        recipe.register();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ArmorSet)) {
            return false;
        }

        ArmorSet set = (ArmorSet) o;
        return this.getName().equals(set.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getName());
    }

    @Override
    public String toString() {
        return "ArmorSet{"
                + this.getName()
                + "}";
    }
}
