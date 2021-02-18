package com.willfp.ecoarmor.sets;

import com.willfp.eco.internal.config.AbstractUndefinedConfig;
import com.willfp.eco.util.SkullUtils;
import com.willfp.eco.util.StringUtils;
import com.willfp.eco.util.display.Display;
import com.willfp.eco.util.recipe.RecipeParts;
import com.willfp.eco.util.recipe.parts.ComplexRecipePart;
import com.willfp.eco.util.recipe.recipes.EcoShapedRecipe;
import com.willfp.ecoarmor.EcoArmorPlugin;
import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.effects.Effects;
import com.willfp.ecoarmor.sets.meta.ArmorSlot;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
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
    @Getter
    private final AbstractUndefinedConfig config;

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
                    @NotNull final AbstractUndefinedConfig config) {
        this.config = config;
        this.name = name;

        for (String definedKey : this.getConfig().getStrings("set-bonus")) {
            String[] split = definedKey.split(":");
            String key = split[0].trim();
            String value = split[1].trim();
            Effect<?> effect = Effects.getByName(key);
            effects.put(effect, ArmorUtils.getEffectValue(value, effect));
        }

        for (String definedKey : this.getConfig().getStrings("advanced-set-bonus")) {
            String[] split = definedKey.split(":");
            String key = split[0].trim();
            String value = split[1].trim();
            Effect<?> effect = Effects.getByName(key);
            advancedEffects.put(effect, ArmorUtils.getEffectValue(value, effect));
        }

        for (String definedKey : this.getConfig().getStrings("potion-effects")) {
            String[] split = definedKey.split(":");
            String key = split[0].trim();
            String value = split[1].trim();
            PotionEffectType type = PotionEffectType.getByName(key.toUpperCase());
            potionEffects.put(type, Integer.parseInt(value));
        }

        for (String definedKey : this.getConfig().getStrings("advanced-potion-effects")) {
            String[] split = definedKey.split(":");
            String key = split[0].trim();
            String value = split[1].trim();
            PotionEffectType type = PotionEffectType.getByName(key.toUpperCase());
            advancedPotionEffects.put(type, Integer.parseInt(value));
        }

        for (ArmorSlot slot : ArmorSlot.values()) {
            ItemStack item = construct(slot.name().toLowerCase(), false);
            items.put(slot, item);

            ItemStack advancedItem = construct(slot.name().toLowerCase(), true);
            advancedItems.put(slot, advancedItem);
        }

        this.advancementShardItem = constructShard();

        if (!this.getConfig().getBool("enabled")) {
            return;
        }

        ArmorSets.addNewSet(this);
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

    private ItemStack construct(@NotNull final String slot,
                                final boolean advanced) {
        String pieceName = slot.toLowerCase();

        Material material = Material.getMaterial(this.getConfig().getString(pieceName + ".material").toUpperCase());
        Map<Enchantment, Integer> enchants = new HashMap<>();

        for (String definedKey : this.getConfig().getStrings(pieceName + ".enchants")) {
            String[] split = definedKey.split(":");
            String key = split[0].trim();
            String value = split[1].trim();
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(key));
            enchants.put(enchantment, Integer.valueOf(value));
        }

        assert material != null;

        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();

        assert meta != null;

        String displayName;
        if (advanced) {
            displayName = this.getConfig().getString(pieceName + ".advanced-name");
        } else {
            displayName = this.getConfig().getString(pieceName + ".name");
        }

        List<ItemFlag> flags = new ArrayList<>();
        for (String flagName : this.getConfig().getStrings(pieceName + ".flags")) {
            ItemFlag flag = ItemFlag.valueOf(flagName.toUpperCase());
            flags.add(flag);
        }
        meta.addItemFlags(flags.toArray(new ItemFlag[0]));

        int data = this.getConfig().getInt(pieceName + ".custom-model-data");
        if (data != -1) {
            meta.setCustomModelData(data);
        }

        boolean unbreakable = this.getConfig().getBool(pieceName + ".unbreakable");
        meta.setUnbreakable(unbreakable);

        List<String> lore = new ArrayList<>();
        for (String loreLine : this.getConfig().getStrings(pieceName + ".lore")) {
            lore.add(Display.PREFIX + StringUtils.translate(loreLine));
        }

        if (advanced) {
            for (String loreLine : this.getConfig().getStrings("advanced-lore")) {
                lore.add(Display.PREFIX + StringUtils.translate(loreLine));
            }
        }

        if (meta instanceof SkullMeta) {
            String base64 = this.getConfig().getString(pieceName + ".skull-texture");
            SkullUtils.setSkullTexture((SkullMeta) meta, base64);
        }

        if (meta instanceof LeatherArmorMeta) {
            String colorString = this.getConfig().getString(pieceName + ".leather-color");
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
        container.set(PLUGIN.getNamespacedKeyFactory().create("effective-durability"), PersistentDataType.INTEGER, this.getConfig().getInt(pieceName + ".effective-durability"));
        if (advanced) {
            container.set(PLUGIN.getNamespacedKeyFactory().create("advanced"), PersistentDataType.INTEGER, 1);
        }
        itemStack.setItemMeta(meta);

        RecipeParts.registerRecipePart(PLUGIN.getNamespacedKeyFactory().create(name.toLowerCase() + "_" + pieceName), new ComplexRecipePart(test -> {
            if (ArmorSlot.getSlot(test) != ArmorSlot.getSlot(itemStack)) {
                return false;
            }
            return Objects.equals(this, ArmorUtils.getSetOnItem(test));
        }, itemStack));


        if (!advanced) {
            constructRecipe(slot, itemStack);
        }

        return itemStack;
    }

    private void constructRecipe(@NotNull final String slot,
                                 @NotNull final ItemStack out) {
        EcoShapedRecipe.Builder builder = EcoShapedRecipe.builder(PLUGIN, this.getName() + "_" + slot).setOutput(out);

        List<String> recipeStrings = this.getConfig().getStrings(slot.toLowerCase() + ".recipe");

        for (int i = 0; i < 9; i++) {
            builder.setRecipePart(i, RecipeParts.lookup(recipeStrings.get(i)));
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
