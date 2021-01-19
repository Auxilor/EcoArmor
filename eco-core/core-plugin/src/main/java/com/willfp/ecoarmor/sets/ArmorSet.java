package com.willfp.ecoarmor.sets;

import com.willfp.eco.util.StringUtils;
import com.willfp.eco.util.recipe.EcoShapedRecipe;
import com.willfp.eco.util.recipe.lookup.RecipePartUtils;
import com.willfp.eco.util.recipe.parts.ComplexRecipePart;
import com.willfp.ecoarmor.EcoArmorPlugin;
import com.willfp.ecoarmor.config.EcoArmorConfigs;
import com.willfp.ecoarmor.display.ArmorDisplay;
import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.effects.Effects;
import com.willfp.ecoarmor.proxy.proxies.SkullProxy;
import com.willfp.ecoarmor.sets.meta.ArmorSlot;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import com.willfp.ecoarmor.util.ProxyUtils;
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

@SuppressWarnings("unchecked")
public class ArmorSet {
    /**
     * Instance of EcoArmor.
     */
    private static final EcoArmorPlugin PLUGIN = EcoArmorPlugin.getInstance();

    /**
     * The name of the set.
     */
    @Getter
    private final String name;

    /**
     * Effects and their strengths.
     */
    @Getter
    private final Map<Effect<?>, Object> effects = new HashMap<>();

    /**
     * Effects and their strengths on advanced armor.
     */
    @Getter
    private final Map<Effect<?>, Object> advancedEffects = new HashMap<>();

    /**
     * Potion effects to be applied on equip.
     */
    @Getter
    private final Map<PotionEffectType, Integer> potionEffects = new HashMap<>();

    /**
     * Items in set.
     */
    private final Map<ArmorSlot, ItemStack> items = new HashMap<>();

    /**
     * Items in advanced set.
     */
    private final Map<ArmorSlot, ItemStack> advancedItems = new HashMap<>();

    /**
     * Advancement shard item.
     */
    @Getter
    private final ItemStack advancementShardItem;

    /**
     * Create a new Armor Set.
     *
     * @param name The name of the set.
     */
    public ArmorSet(@NotNull final String name) {
        this.name = name;

        for (String effectName : EcoArmorConfigs.SETS.getConfig().getConfigurationSection(name + ".set-bonus").getKeys(false)) {
            Effect<?> effect = Effects.getByName(effectName);
            Object value = EcoArmorConfigs.SETS.getConfig().get(name + ".set-bonus." + effectName);
            effects.put(effect, value);
        }

        for (String effectName : EcoArmorConfigs.SETS.getConfig().getConfigurationSection(name + ".advanced-set-bonus").getKeys(false)) {
            Effect<?> effect = Effects.getByName(effectName);
            Object value = EcoArmorConfigs.SETS.getConfig().get(name + ".advanced-set-bonus." + effectName);
            advancedEffects.put(effect, value);
        }

        for (ArmorSlot slot : ArmorSlot.values()) {
            ItemStack item = construct(slot.name().toLowerCase(), false);
            items.put(slot, item);

            ItemStack advancedItem = construct(slot.name().toLowerCase(), true);
            advancedItems.put(slot, advancedItem);
        }

        this.advancementShardItem = constructShard();

        ArmorSets.addNewSet(this);
    }

    private ItemStack constructShard() {
        ItemStack shardItem = new ItemStack(Material.PRISMARINE_SHARD);
        ItemMeta shardMeta = shardItem.getItemMeta();
        assert shardMeta != null;
        shardMeta.setDisplayName(EcoArmorConfigs.SETS.getString(name + ".advancement-shard-name"));

        shardMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        shardMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        List<String> shardLore = new ArrayList<>();
        for (String loreLine : EcoArmorConfigs.SETS.getStrings(name + ".advancement-shard-lore")) {
            shardLore.add(ArmorDisplay.PREFIX + StringUtils.translate(loreLine));
        }

        shardMeta.setLore(shardLore);
        shardMeta.getPersistentDataContainer().set(PLUGIN.getNamespacedKeyFactory().create("advancement-shard"), PersistentDataType.STRING, name);

        shardItem.setItemMeta(shardMeta);

        if (EcoArmorConfigs.SETS.getBool(name + ".shard-craftable")) {
            EcoShapedRecipe.Builder builder = EcoShapedRecipe.builder(PLUGIN, this.getName() + "_shard").setOutput(shardItem);

            List<String> recipeStrings = EcoArmorConfigs.SETS.getStrings(name + ".shard-recipe");

            for (int i = 0; i < 9; i++) {
                builder.setRecipePart(i, RecipePartUtils.lookup(recipeStrings.get(i)));
            }

            EcoShapedRecipe recipe = builder.build();
            recipe.register();
        }

        return shardItem;
    }

    private ItemStack construct(@NotNull final String slot,
                                final boolean advanced) {
        String pieceName = slot.toLowerCase();

        Material material = Material.getMaterial(EcoArmorConfigs.SETS.getString(name + "." + pieceName + ".material").toUpperCase());
        Map<Enchantment, Integer> enchants = new HashMap<>();

        for (String enchantKey : EcoArmorConfigs.SETS.getConfig().getConfigurationSection(name + "." + pieceName + ".enchants").getKeys(false)) {
            int level = EcoArmorConfigs.SETS.getInt(name + "." + pieceName + ".enchants." + enchantKey);
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantKey));
            enchants.put(enchantment, level);
        }

        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();

        assert meta != null;

        String displayName;
        if (advanced) {
            displayName = EcoArmorConfigs.SETS.getString(name + "." + pieceName + ".advanced-name");
        } else {
            displayName = EcoArmorConfigs.SETS.getString(name + "." + pieceName + ".name");
        }

        List<String> lore = new ArrayList<>();
        for (String loreLine : EcoArmorConfigs.SETS.getStrings(name + "." + pieceName + ".lore")) {
            lore.add(ArmorDisplay.PREFIX + StringUtils.translate(loreLine));
        }

        if (advanced) {
            for (String loreLine : EcoArmorConfigs.SETS.getStrings(name + ".advanced-lore")) {
                lore.add(ArmorDisplay.PREFIX + StringUtils.translate(loreLine));
            }
        }

        if (meta instanceof SkullMeta) {
            String base64 = EcoArmorConfigs.SETS.getString(name + "." + pieceName + ".skull-texture");
            ProxyUtils.getProxy(SkullProxy.class).setTexture((SkullMeta) meta, base64);
        }

        if (meta instanceof LeatherArmorMeta) {
            String colorString = EcoArmorConfigs.SETS.getString(name + "." + pieceName + ".leather-color");
            java.awt.Color awtColor = java.awt.Color.decode(colorString);
            Color color = Color.fromRGB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
            ((LeatherArmorMeta) meta).setColor(color);

            meta.addItemFlags(ItemFlag.HIDE_DYE);
        }

        meta.setDisplayName(displayName);

        meta.setLore(lore);

        enchants.forEach((enchantment, integer) -> meta.addEnchant(enchantment, integer, true));
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(PLUGIN.getNamespacedKeyFactory().create("set"), PersistentDataType.STRING, name);
        container.set(PLUGIN.getNamespacedKeyFactory().create("tier"), PersistentDataType.STRING, "default");
        container.set(PLUGIN.getNamespacedKeyFactory().create("effective-durability"), PersistentDataType.INTEGER, EcoArmorConfigs.SETS.getInt(name + "." + pieceName + ".effective-durability"));
        if (advanced) {
            container.set(PLUGIN.getNamespacedKeyFactory().create("advanced"), PersistentDataType.INTEGER, 1);
        }
        itemStack.setItemMeta(meta);


        RecipePartUtils.registerLookup("ecoarmor:set_" + name.toLowerCase() + "_" + pieceName, s -> new ComplexRecipePart(test -> {
            if (ArmorSlot.getSlot(test) != ArmorSlot.getSlot(itemStack)) {
                return false;
            }
            return Objects.equals(this, ArmorUtils.getSetOnItem(test));
        }, itemStack));


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

    /**
     * Get item stack from slot.
     *
     * @param slot The slot.
     * @return The item.
     */
    public ItemStack getItemStack(@NotNull final ArmorSlot slot) {
        return items.get(slot);
    }

    /**
     * Get item stack from slot.
     *
     * @param slot The slot.
     * @return The item.
     */
    public ItemStack getAdvancedItemStack(@NotNull final ArmorSlot slot) {
        return advancedItems.get(slot);
    }

    /**
     * Get effect strength of effect.
     *
     * @param effect The effect to query.
     * @param <T>    The type of the effect value.
     * @return The strength.
     */
    public <T> T getEffectStrength(@NotNull final Effect<T> effect) {
        return (T) effects.get(effect);
    }

    /**
     * Get effect strength of effect on advanced armor.
     *
     * @param effect The effect to query.
     * @param <T>    The type of the effect value.
     * @return The strength.
     */
    public <T> T getAdvancedEffectStrength(@NotNull final Effect<T> effect) {
        return (T) advancedEffects.get(effect);
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
