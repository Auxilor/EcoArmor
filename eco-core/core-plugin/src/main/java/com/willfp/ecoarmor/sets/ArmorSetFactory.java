package com.willfp.ecoarmor.sets;

import com.willfp.eco.core.config.Config;
import com.willfp.eco.core.config.JSONConfig;
import com.willfp.eco.core.display.Display;
import com.willfp.eco.core.items.CustomItem;
import com.willfp.eco.core.items.builder.ItemBuilder;
import com.willfp.eco.core.items.builder.ItemStackBuilder;
import com.willfp.eco.core.items.builder.LeatherArmorBuilder;
import com.willfp.eco.core.items.builder.SkullBuilder;
import com.willfp.eco.core.recipe.Recipes;
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

public class ArmorSetFactory {

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
    private final JSONConfig config;

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
     */
    public ArmorSetFactory(@NotNull final JSONConfig config) {
        this.config = config;
        this.name = config.getString("name");

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

        for (JSONConfig cfg : this.getConfig().getSubsections("effects")) {
            Effect<?> effect = Effects.getByName(cfg.getString("id"));
            Object value = cfg.get("args");
            effects.put(effect, value);
        }

        for (JSONConfig cfg : this.getConfig().getSubsections("advanced-effects")) {
            Effect<?> effect = Effects.getByName(cfg.getString("id"));
            Object value = cfg.get("args");
            advancedEffects.put(effect, value);
        }

        for (JSONConfig cfg : this.getConfig().getSubsections("potion-effects")) {
            PotionEffectType effect = PotionEffectType.getByName(cfg.getString("id").toUpperCase());
            int level = cfg.getInt("level");
            potionEffects.put(effect, level);
        }

        for (JSONConfig cfg : this.getConfig().getSubsections("advanced-potion-effects")) {
            PotionEffectType effect = PotionEffectType.getByName(cfg.getString("id").toUpperCase());
            int level = cfg.getInt("level");
            advancedPotionEffects.put(effect, level);
        }

        for (ArmorSlot slot : ArmorSlot.values()) {
            ItemStack item = construct(slot, (JSONConfig) this.getConfig().getSubsection(slot.name().toLowerCase()), false);
            items.put(slot, item);
            if (this.getConfig().getBool("enabled")) {
                constructRecipe(slot, this.getConfig().getSubsection(slot.name().toLowerCase()), item);
            }

            ItemStack advancedItem = construct(slot, (JSONConfig) this.getConfig().getSubsection(slot.name().toLowerCase()), true);
            advancedItems.put(slot, advancedItem);
        }

        this.advancementShardItem = constructShard();
    }

    private ItemStack constructShard() {
        ItemStack shard = new ItemStackBuilder(Objects.requireNonNull(Material.getMaterial(PLUGIN.getConfigYml().getString("advancement-shard-material").toUpperCase())))
                .setDisplayName(this.getConfig().getString("advancement-shard-name"))
                .addEnchantment(Enchantment.DURABILITY, 3)
                .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                .addLoreLines(this.getConfig().getStrings("advancement-shard-lore"))
                .writeMetaKey(PLUGIN.getNamespacedKeyFactory().create("advancement-shard"), PersistentDataType.STRING, name)
                .build();

        if (this.getConfig().getBool("shard-craftable")) {
            Recipes.createAndRegisterRecipe(PLUGIN,
                    this.getName() + "_shard",
                    shard,
                    this.getConfig().getStrings("shard-recipe"));
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

        builder.setDisplayName(advanced ? slotConfig.getString("advanced-name") : slotConfig.getString("name"))
                .addItemFlag(
                        slotConfig.getStrings("flags").stream()
                                .map(s -> ItemFlag.valueOf(s.toUpperCase()))
                                .toArray(ItemFlag[]::new)
                )
                .setUnbreakable(slotConfig.getBool("unbreakable"))
                .addLoreLines(slotConfig.getStrings("lore").stream().map(s -> Display.PREFIX + s).collect(Collectors.toList()))
                .addLoreLines(() -> {
                    if (advanced) {
                        return slotConfig.getStrings("advanced-lore").stream().map(s -> Display.PREFIX + s).collect(Collectors.toList());
                    } else {
                        return null;
                    }
                })
                .setCustomModelData(() -> {
                    int data = slotConfig.getInt("custom-model-data");
                    return data != -1 ? data : null;
                })
                .setDisplayName(() -> advanced ? slotConfig.getString("advanced-name") : slotConfig.getString("name"));


        if (builder instanceof SkullBuilder skullBuilder) {
            this.skullBase64 = slotConfig.getString("skull-texture");
            skullBuilder.setSkullTexture(skullBase64);
        }

        if (builder instanceof LeatherArmorBuilder leatherArmorBuilder) {
            String colorString = slotConfig.getString("leather-color");
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
                PLUGIN.getNamespacedKeyFactory().create("set"),
                PersistentDataType.STRING,
                name
        ).writeMetaKey(
                PLUGIN.getNamespacedKeyFactory().create("effective-durability"),
                PersistentDataType.INTEGER,
                slotConfig.getInt("effective-durability")
        );

        ItemStack itemStack = builder.build();

        ArmorUtils.setAdvanced(itemStack, advanced);
        Tier defaultTier = Tiers.getByName(slotConfig.getString("default-tier"));
        if (defaultTier == null) {
            Bukkit.getLogger().warning("Default tier specified in " + this.name + " " + slot.name().toLowerCase() + " is invalid! Defaulting to 'default'");
            ArmorUtils.setTier(itemStack, Tiers.DEFAULT);
        } else {
            ArmorUtils.setTier(itemStack, defaultTier);
        }

        if (advanced) {
            new CustomItem(PLUGIN.getNamespacedKeyFactory().create("set_" + name.toLowerCase() + "_" + slot.name().toLowerCase() + "_advanced"), test -> {
                if (ArmorSlot.getSlot(test) != ArmorSlot.getSlot(itemStack)) {
                    return false;
                }
                if (!ArmorUtils.isAdvanced(itemStack)) {
                    return false;
                }
                return Objects.equals(this.getName(), ArmorUtils.getSetOnItem(test).getName());
            }, itemStack).register();
        } else {
            new CustomItem(PLUGIN.getNamespacedKeyFactory().create("set_" + name.toLowerCase() + "_" + slot.name().toLowerCase()), test -> {
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
                s = s.replace("%tier%", Tiers.DEFAULT.getDisplayName());
                lore.add(s);
            }

            meta.setLore(lore);
            formattedOut.setItemMeta(meta);

            Recipes.createAndRegisterRecipe(
                    PLUGIN,
                    this.getName() + "_" + slot.name().toLowerCase(),
                    formattedOut,
                    slotConfig.getStrings("recipe")
            );
        }
    }

    /**
     * Create the Armor Set.
     *
     * @return The set.
     */
    public ArmorSet create() {
        return new ArmorSet(
                name,
                conditions,
                effects,
                advancedEffects,
                potionEffects,
                advancedPotionEffects,
                skullBase64,
                items,
                advancedItems,
                advancementShardItem
        );
    }
}
