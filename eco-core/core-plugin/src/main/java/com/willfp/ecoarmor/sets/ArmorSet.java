package com.willfp.ecoarmor.sets;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.config.interfaces.JSONConfig;
import com.willfp.eco.core.display.Display;
import com.willfp.eco.core.fast.FastItemStack;
import com.willfp.eco.core.items.CustomItem;
import com.willfp.eco.core.items.builder.ItemBuilder;
import com.willfp.eco.core.items.builder.ItemStackBuilder;
import com.willfp.eco.core.items.builder.LeatherArmorBuilder;
import com.willfp.eco.core.items.builder.SkullBuilder;
import com.willfp.eco.core.recipe.Recipes;
import com.willfp.eco.util.StringUtils;
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
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class ArmorSet {
    /**
     * Instance of EcoArmor.
     */
    @Getter(AccessLevel.PRIVATE)
    private final EcoPlugin plugin;

    /**
     * The config of the set.
     */
    @Getter(AccessLevel.PRIVATE)
    private final JSONConfig config;

    /**
     * The name of the set.
     */
    @Getter
    private final String name;

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
     * @param config The set's config.
     * @param plugin Instance of EcoArmor.
     */
    public ArmorSet(@NotNull final JSONConfig config,
                    @NotNull final EcoPlugin plugin) {
        this.config = config;
        this.plugin = plugin;
        this.name = config.getString("name");

        for (JSONConfig cfg : this.getConfig().getSubsections("conditions")) {
            Condition<?> effect = Conditions.getByName(cfg.getString("id"));
            Object value = cfg.get("args");
            conditions.put(effect, value);
        }

        for (JSONConfig cfg : this.getConfig().getSubsections("effects")) {
            Effect<?> effect = Effects.getByName(cfg.getString("id"));
            Object value = cfg.get("args");
            effects.put(effect, value);
        }

        for (JSONConfig cfg : this.getConfig().getSubsections("advancedEffects")) {
            Effect<?> effect = Effects.getByName(cfg.getString("id"));
            Object value = cfg.get("args");
            advancedEffects.put(effect, value);
        }

        for (JSONConfig cfg : this.getConfig().getSubsections("potionEffects")) {
            PotionEffectType effect = PotionEffectType.getByName(cfg.getString("id").toUpperCase());
            int level = cfg.getInt("level");
            potionEffects.put(effect, level);
        }

        for (JSONConfig cfg : this.getConfig().getSubsections("advancedPotionEffects")) {
            PotionEffectType effect = PotionEffectType.getByName(cfg.getString("id").toUpperCase());
            int level = cfg.getInt("level");
            advancedPotionEffects.put(effect, level);
        }

        for (ArmorSlot slot : ArmorSlot.values()) {
            ItemStack item = construct(slot, this.getConfig().getSubsection(slot.name().toLowerCase()), false);
            items.put(slot, item);
            constructRecipe(slot, this.getConfig().getSubsection(slot.name().toLowerCase()), item);

            ItemStack advancedItem = construct(slot, this.getConfig().getSubsection(slot.name().toLowerCase()), true);
            advancedItems.put(slot, advancedItem);
        }

        this.advancementShardItem = constructShard();
    }

    private ItemStack constructShard() {
        ItemStack shard = new ItemStackBuilder(Objects.requireNonNull(Material.getMaterial(this.getPlugin().getConfigYml().getString("advancement-shard-material").toUpperCase())))
                .setDisplayName(this.getConfig().getString("advancementShardName"))
                .addEnchantment(Enchantment.DURABILITY, 3)
                .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                .addLoreLines(this.getConfig().getStrings("advancementShardLore"))
                .writeMetaKey(this.getPlugin().getNamespacedKeyFactory().create("advancement-shard"), PersistentDataType.STRING, name)
                .build();

        if (this.getConfig().getBool("shardCraftable")) {
            Recipes.createAndRegisterRecipe(this.getPlugin(),
                    this.getName() + "_shard",
                    shard,
                    this.getConfig().getStrings("shardRecipe"));
        }

        return shard;
    }

    private ItemStack construct(@NotNull final ArmorSlot slot,
                                @NotNull final JSONConfig slotConfig,
                                final boolean advanced) {
        Material material = Material.getMaterial(slotConfig.getString("material").toUpperCase());

        assert material != null;

        ItemBuilder builder;

        builder = switch (material) {
            case PLAYER_HEAD -> new SkullBuilder();
            case LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS -> new LeatherArmorBuilder(material);
            default -> new ItemStackBuilder(material);
        };

        builder.setDisplayName(advanced ? slotConfig.getString("advancedName") : slotConfig.getString("name"))
                .addItemFlag(
                        slotConfig.getStrings("flags").stream()
                                .map(s -> ItemFlag.valueOf(s.toUpperCase()))
                                .toArray(ItemFlag[]::new)
                )
                .setUnbreakable(slotConfig.getBool("unbreakable"))
                .addLoreLines(slotConfig.getStrings("lore").stream().map(s -> Display.PREFIX + s).collect(Collectors.toList()))
                .addLoreLines(() -> {
                    if (advanced) {
                        return this.getConfig().getStrings("advancedLore").stream().map(s -> Display.PREFIX + s).collect(Collectors.toList());
                    } else {
                        return null;
                    }
                })
                .setCustomModelData(() -> {
                    int data = slotConfig.getInt("customModelData");
                    return data != -1 ? data : null;
                })
                .setDisplayName(() -> advanced ? slotConfig.getString("advancedName") : slotConfig.getString("name"));


        if (builder instanceof SkullBuilder skullBuilder) {
            this.skullBase64 = slotConfig.getString("skullTexture");
            skullBuilder.setSkullTexture(skullBase64);
        }

        if (builder instanceof LeatherArmorBuilder leatherArmorBuilder) {
            String colorString = slotConfig.getString("leatherColor");
            java.awt.Color awtColor = java.awt.Color.decode(colorString);
            leatherArmorBuilder.setColor(awtColor);
            builder.addItemFlag(ItemFlag.HIDE_DYE);
        }


        Map<Enchantment, Integer> enchants = new HashMap<>();

        for (JSONConfig enchantSection : slotConfig.getSubsections("enchants")) {
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantSection.getString("id")));
            int level = enchantSection.getInt("level");
            enchants.put(enchantment, level);
        }

        enchants.forEach(builder::addEnchantment);

        builder.writeMetaKey(
                this.getPlugin().getNamespacedKeyFactory().create("set"),
                PersistentDataType.STRING,
                name
        ).writeMetaKey(
                this.getPlugin().getNamespacedKeyFactory().create("effective-durability"),
                PersistentDataType.INTEGER,
                slotConfig.getInt("effectiveDurability")
        );

        ItemStack itemStack = builder.build();

        ArmorUtils.setAdvanced(itemStack, advanced);
        Tier defaultTier = Tiers.getByName(slotConfig.getString("defaultTier"));
        if (defaultTier == null) {
            Bukkit.getLogger().warning("Default tier specified in " + this.name + " " + slot.name().toLowerCase() + " is invalid! Defaulting to 'default'");
            ArmorUtils.setTier(itemStack, Tiers.getDefaultTier());
        } else {
            ArmorUtils.setTier(itemStack, defaultTier);
        }

        if (advanced) {
            new CustomItem(this.getPlugin().getNamespacedKeyFactory().create("set_" + name.toLowerCase() + "_" + slot.name().toLowerCase() + "_advanced"), test -> {
                if (ArmorSlot.getSlot(test) != ArmorSlot.getSlot(itemStack)) {
                    return false;
                }
                if (!ArmorUtils.isAdvanced(itemStack)) {
                    return false;
                }
                return Objects.equals(this.getName(), ArmorUtils.getSetOnItem(test).getName());
            }, itemStack).register();
        } else {
            new CustomItem(this.getPlugin().getNamespacedKeyFactory().create("set_" + name.toLowerCase() + "_" + slot.name().toLowerCase()), test -> {
                if (ArmorSlot.getSlot(test) != ArmorSlot.getSlot(itemStack)) {
                    return false;
                }
                if (ArmorUtils.isAdvanced(itemStack)) {
                    return false;
                }
                return Objects.equals(this.getName(), ArmorUtils.getSetOnItem(test).getName());
            }, itemStack).register();
        }

        return itemStack;
    }

    private void constructRecipe(@NotNull final ArmorSlot slot,
                                 @NotNull final Config slotConfig,
                                 @NotNull final ItemStack out) {
        if (slotConfig.getBool("craftable")) {
            ItemStack formattedOut = out.clone();
            ItemMeta meta = formattedOut.getItemMeta();
            assert meta != null;
            assert meta.getLore() != null;

            List<String> lore = new ArrayList<>();

            for (String s : meta.getLore()) {
                s = s.replace("%tier%", Tiers.getDefaultTier().getDisplayName());
                lore.add(s);
            }

            meta.setLore(lore);
            formattedOut.setItemMeta(meta);

            Recipes.createAndRegisterRecipe(
                    this.getPlugin(),
                    this.getName() + "_" + slot.name().toLowerCase(),
                    formattedOut,
                    slotConfig.getStrings("recipe")
            );
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

        if (!(o instanceof ArmorSet set)) {
            return false;
        }

        return this.name.equals(set.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    @Override
    public String toString() {
        return "ArmorSet{"
                + this.name
                + "}";
    }
}
