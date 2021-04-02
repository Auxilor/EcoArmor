package com.willfp.ecoarmor.sets;

import com.willfp.eco.util.SkullUtils;
import com.willfp.eco.util.StringUtils;
import com.willfp.eco.util.config.Config;
import com.willfp.eco.util.display.Display;
import com.willfp.eco.util.recipe.RecipeParts;
import com.willfp.eco.util.recipe.parts.ComplexRecipePart;
import com.willfp.eco.util.recipe.recipes.EcoShapedRecipe;
import com.willfp.ecoarmor.EcoArmorPlugin;
import com.willfp.ecoarmor.conditions.Condition;
import com.willfp.ecoarmor.conditions.Conditions;
import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.effects.Effects;
import com.willfp.ecoarmor.sets.meta.ArmorSlot;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import com.willfp.ecoarmor.upgrades.Tier;
import com.willfp.ecoarmor.upgrades.Tiers;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
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
import org.jetbrains.annotations.Nullable;

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
     * The config of the set.
     */
    @Getter(AccessLevel.PRIVATE)
    private final Config config;

    /**
     * Conditions and their values.
     */
    @Getter
    private final Map<Condition<?>, Object> conditions = new HashMap<>();

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
     * Potion effects to be applied on equipping advanced.
     */
    @Getter
    private final Map<PotionEffectType, Integer> advancedPotionEffects = new HashMap<>();

    /**
     * The base64 texture of a skull used as a helmet.
     * <p>
     * Null if no skull.
     */
    @Getter
    @Nullable
    private String skullBase64;

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
     * @param name   The name of the set.
     * @param config The set's config.
     */
    public ArmorSet(@NotNull final String name,
                    @NotNull final Config config) {
        this.config = config;
        this.name = name;

        for (String definedKey : this.getConfig().getStrings("conditions")) {
            String[] split = definedKey.split(":");
            String key = split[0].trim();
            String value = split[1].trim();
            Condition<?> condition = Conditions.getByName(key);
            if (condition == null) {
                Bukkit.getLogger().warning("Invalid condition specified in " + this.name);
            } else {
                conditions.put(condition, ArmorUtils.getConditionValue(value, condition));
            }
        }

        for (String definedKey : this.getConfig().getStrings("set-bonus")) {
            String[] split = definedKey.split(":");
            String key = split[0].trim();
            String value = split[1].trim();
            Effect<?> effect = Effects.getByName(key);
            if (effect == null) {
                Bukkit.getLogger().warning("Invalid effect specified in " + this.name);
            } else {
                effects.put(effect, ArmorUtils.getEffectValue(value, effect));
            }
        }

        for (String definedKey : this.getConfig().getStrings("advanced-set-bonus")) {
            String[] split = definedKey.split(":");
            String key = split[0].trim();
            String value = split[1].trim();
            Effect<?> effect = Effects.getByName(key);
            if (effect == null) {
                Bukkit.getLogger().warning("Invalid advanced effect specified in " + this.name);
            } else {
                advancedEffects.put(effect, ArmorUtils.getEffectValue(value, effect));
            }
        }

        for (String definedKey : this.getConfig().getStrings("potion-effects")) {
            String[] split = definedKey.split(":");
            String key = split[0].trim();
            String value = split[1].trim();
            PotionEffectType type = PotionEffectType.getByName(key.toUpperCase());
            if (type == null) {
                Bukkit.getLogger().warning("Invalid potion effect specified in " + this.name);
            } else {
                potionEffects.put(type, Integer.parseInt(value));
            }
        }

        for (String definedKey : this.getConfig().getStrings("advanced-potion-effects")) {
            String[] split = definedKey.split(":");
            String key = split[0].trim();
            String value = split[1].trim();
            PotionEffectType type = PotionEffectType.getByName(key.toUpperCase());
            if (type == null) {
                Bukkit.getLogger().warning("Invalid advanced potion effect specified in " + this.name);
            } else {
                advancedPotionEffects.put(type, Integer.parseInt(value));
            }
        }

        for (ArmorSlot slot : ArmorSlot.values()) {
            ItemStack item = construct(slot, this.getConfig().getSubsection(slot.name().toLowerCase()), false);
            items.put(slot, item);
            constructRecipe(slot, this.getConfig().getSubsection(slot.name().toLowerCase()), item);

            ItemStack advancedItem = construct(slot, this.getConfig().getSubsection(slot.name().toLowerCase()), true);
            advancedItems.put(slot, advancedItem);
        }

        if (this.getConfig().getBool("enabled")) {
            ArmorSets.addNewSet(this);
        }

        this.advancementShardItem = constructShard();
    }

    private ItemStack constructShard() {
        ItemStack shardItem = new ItemStack(Objects.requireNonNull(Material.getMaterial(PLUGIN.getConfigYml().getString("advancement-shard-material").toUpperCase())));
        ItemMeta shardMeta = shardItem.getItemMeta();
        assert shardMeta != null;
        shardMeta.setDisplayName(this.getConfig().getString("advancement-shard-name"));

        shardMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        shardMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        List<String> shardLore = new ArrayList<>();
        for (String loreLine : this.getConfig().getStrings("advancement-shard-lore")) {
            shardLore.add(Display.PREFIX + StringUtils.translate(loreLine));
        }

        shardMeta.setLore(shardLore);
        shardMeta.getPersistentDataContainer().set(PLUGIN.getNamespacedKeyFactory().create("advancement-shard"), PersistentDataType.STRING, name);

        shardItem.setItemMeta(shardMeta);

        if (this.getConfig().getBool("shard-craftable")) {
            EcoShapedRecipe.Builder builder = EcoShapedRecipe.builder(PLUGIN, this.getName() + "_shard").setOutput(shardItem);

            List<String> recipeStrings = this.getConfig().getStrings("shard-recipe");

            for (int i = 0; i < 9; i++) {
                builder.setRecipePart(i, RecipeParts.lookup(recipeStrings.get(i)));
            }

            EcoShapedRecipe recipe = builder.build();
            recipe.register();
        }

        return shardItem;
    }

    private ItemStack construct(@NotNull final ArmorSlot slot,
                                @NotNull final Config slotConfig,
                                final boolean advanced) {
        Material material = Material.getMaterial(slotConfig.getString("material").toUpperCase());
        Map<Enchantment, Integer> enchants = new HashMap<>();

        for (String definedKey : slotConfig.getStrings("enchants")) {
            String[] split = definedKey.split(":");
            String key = split[0].trim();
            String value = split[1].trim();
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(key));
            if (enchantment == null) {
                Bukkit.getLogger().warning("Invalid enchantment specified in " + this.name + " " + slot.name().toLowerCase());
            } else {
                enchants.put(enchantment, Integer.valueOf(value));
            }
        }

        assert material != null;

        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();

        assert meta != null;

        String displayName;
        if (advanced) {
            displayName = slotConfig.getString("advanced-name");
        } else {
            displayName = slotConfig.getString("name");
        }

        List<ItemFlag> flags = new ArrayList<>();
        for (String flagName : slotConfig.getStrings("flags")) {
            ItemFlag flag = ItemFlag.valueOf(flagName.toUpperCase());
            flags.add(flag);
        }
        meta.addItemFlags(flags.toArray(new ItemFlag[0]));

        int data = slotConfig.getInt("custom-model-data");
        if (data != -1) {
            meta.setCustomModelData(data);
        }

        boolean unbreakable = slotConfig.getBool("unbreakable");
        meta.setUnbreakable(unbreakable);

        List<String> lore = new ArrayList<>();
        for (String loreLine : slotConfig.getStrings("lore")) {
            lore.add(Display.PREFIX + StringUtils.translate(loreLine));
        }

        if (advanced) {
            for (String loreLine : this.getConfig().getStrings("advanced-lore")) {
                lore.add(Display.PREFIX + StringUtils.translate(loreLine));
            }
        }

        if (meta instanceof SkullMeta) {
            this.skullBase64 = slotConfig.getString("skull-texture");
            SkullUtils.setSkullTexture((SkullMeta) meta, skullBase64);
        }

        if (meta instanceof LeatherArmorMeta) {
            String colorString = slotConfig.getString("leather-color");
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
        container.set(PLUGIN.getNamespacedKeyFactory().create("effective-durability"), PersistentDataType.INTEGER, slotConfig.getInt("effective-durability"));
        itemStack.setItemMeta(meta);

        ArmorUtils.setAdvanced(itemStack, advanced);
        Tier defaultTier = Tiers.getByName(slotConfig.getString("default-tier"));
        if (defaultTier == null) {
            Bukkit.getLogger().warning("Default tier specified in " + this.name + " " + slot.name().toLowerCase() + " is invalid! Defaulting to 'default'");
            ArmorUtils.setTier(itemStack, Tiers.DEFAULT);
        } else {
            ArmorUtils.setTier(itemStack, defaultTier);
        }

        if (advanced) {
            RecipeParts.registerRecipePart(PLUGIN.getNamespacedKeyFactory().create("set_" + name.toLowerCase() + "_" + slot.name().toLowerCase() + "_advanced"), new ComplexRecipePart(test -> {
                if (ArmorSlot.getSlot(test) != ArmorSlot.getSlot(itemStack)) {
                    return false;
                }
                if (!ArmorUtils.isAdvanced(itemStack)) {
                    return false;
                }
                return Objects.equals(this, ArmorUtils.getSetOnItem(test));
            }, itemStack));
        } else {
            RecipeParts.registerRecipePart(PLUGIN.getNamespacedKeyFactory().create("set_" + name.toLowerCase() + "_" + slot.name().toLowerCase()), new ComplexRecipePart(test -> {
                if (ArmorSlot.getSlot(test) != ArmorSlot.getSlot(itemStack)) {
                    return false;
                }
                if (ArmorUtils.isAdvanced(itemStack)) {
                    return false;
                }
                return Objects.equals(this, ArmorUtils.getSetOnItem(test));
            }, itemStack));
        }

        return itemStack;
    }

    private void constructRecipe(@NotNull final ArmorSlot slot,
                                 @NotNull final Config slotConfig,
                                 @NotNull final ItemStack out) {
        if (slotConfig.getBool("craftable")) {
            EcoShapedRecipe.Builder builder = EcoShapedRecipe.builder(PLUGIN, this.getName() + "_" + slot.name().toLowerCase()).setOutput(out);

            List<String> recipeStrings = slotConfig.getStrings("recipe");

            for (int i = 0; i < 9; i++) {
                builder.setRecipePart(i, RecipeParts.lookup(recipeStrings.get(i)));
            }

            EcoShapedRecipe recipe = builder.build();
            recipe.register();
        }
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
     * Get condition value of effect.
     *
     * @param condition The condition to query.
     * @param <T>       The type of the condition value.
     * @return The value.
     */
    @Nullable
    public <T> T getConditionValue(@NotNull final Condition<T> condition) {
        return (T) conditions.get(condition);
    }

    /**
     * Get effect strength of effect.
     *
     * @param effect The effect to query.
     * @param <T>    The type of the effect value.
     * @return The strength.
     */
    @Nullable
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
    @Nullable
    public <T> T getAdvancedEffectStrength(@NotNull final Effect<T> effect) {
        Object strength = advancedEffects.get(effect);
        if (strength instanceof Integer) {
            if (effect.getTypeClass().equals(Double.class)) {
                strength = (double) (int) strength;
            }
        }

        return (T) strength;
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
