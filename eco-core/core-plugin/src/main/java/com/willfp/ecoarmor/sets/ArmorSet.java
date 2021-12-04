package com.willfp.ecoarmor.sets;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.display.Display;
import com.willfp.eco.core.items.CustomItem;
import com.willfp.eco.core.items.Items;
import com.willfp.eco.core.items.builder.ItemBuilder;
import com.willfp.eco.core.items.builder.ItemStackBuilder;
import com.willfp.eco.core.items.builder.LeatherArmorBuilder;
import com.willfp.eco.core.items.builder.SkullBuilder;
import com.willfp.eco.core.recipe.Recipes;
import com.willfp.ecoarmor.sets.meta.ArmorSlot;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import com.willfp.ecoarmor.upgrades.Tier;
import com.willfp.ecoarmor.upgrades.Tiers;
import com.willfp.libreforge.Holder;
import com.willfp.libreforge.conditions.Conditions;
import com.willfp.libreforge.conditions.ConfiguredCondition;
import com.willfp.libreforge.effects.ConfiguredEffect;
import com.willfp.libreforge.effects.Effects;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/*
TODO: Split off ArmorSet between two separate armorset objects (one advanced, one not)
 */
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
    private final Config config;

    /**
     * The name of the set.
     */
    @Getter
    private final String name;

    /**
     * The advanced holder.
     */
    @Getter
    private final Holder advancedHoler;

    /**
     * The regular holder.
     */
    @Getter
    private final Holder regularHolder;

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
    public ArmorSet(@NotNull final Config config,
                    @NotNull final EcoPlugin plugin) {
        this.config = config;
        this.plugin = plugin;
        this.name = config.getString("name");

        Set<ConfiguredCondition> conditions = new HashSet<>();
        for (Config cfg : this.getConfig().getSubsections("conditions")) {
            ConfiguredCondition conf = Conditions.compile(cfg, "Armor Set " + this.name);
            if (conf != null) {
                conditions.add(conf);
            }
        }

        Set<ConfiguredEffect> effects = new HashSet<>();
        for (Config cfg : this.getConfig().getSubsections("effects")) {
            ConfiguredEffect conf = Effects.compile(cfg, "Armor Set " + this.name);
            if (conf != null) {
                effects.add(conf);
            }
        }

        Set<ConfiguredEffect> advancedEffects = new HashSet<>();
        for (Config cfg : this.getConfig().getSubsections("advancedEffects")) {
            ConfiguredEffect conf = Effects.compile(cfg, "Armor Set " + this.name + " (Advanced)");
            if (conf != null) {
                effects.add(conf);
            }
        }

        this.regularHolder = new RegularHolder(conditions, effects);
        this.advancedHoler = new AdvancedHolder(conditions, advancedEffects);

        ArmorSets.addNewSet(this);

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
        List<String> shardLore = this.getConfig().getStrings("advancementShardLore");
        shardLore.replaceAll(s -> Display.PREFIX + s);

        ItemStack shard = new ItemStackBuilder(Items.lookup(this.getPlugin().getConfigYml().getString("advancement-shard-material").toLowerCase()).getItem())
                .setDisplayName(this.getConfig().getString("advancementShardName"))
                .addEnchantment(Enchantment.DURABILITY, 3)
                .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                .addLoreLines(shardLore)
                .writeMetaKey(this.getPlugin().getNamespacedKeyFactory().create("advancement-shard"), PersistentDataType.STRING, name)
                .build();

        if (this.getConfig().getBool("shardCraftable")) {
            Recipes.createAndRegisterRecipe(this.getPlugin(),
                    this.getName() + "_shard",
                    shard,
                    this.getConfig().getStrings("shardRecipe"));
        }

        new CustomItem(
                this.getPlugin().getNamespacedKeyFactory().create("shard_" + name.toLowerCase()),
                test -> this.equals(ArmorUtils.getShardSet(test)),
                shard
        ).register();

        return shard;
    }

    private ItemStack construct(@NotNull final ArmorSlot slot,
                                @NotNull final Config slotConfig,
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

        for (Config enchantSection : slotConfig.getSubsections("enchants")) {
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantSection.getString("id")));
            if (enchantment == null) {
                continue;
            }
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
                if (ArmorUtils.getSetOnItem(test) == null) {
                    return false;
                }

                return Objects.equals(this, ArmorUtils.getSetOnItem(test));
            }, itemStack).register();
        } else {
            new CustomItem(this.getPlugin().getNamespacedKeyFactory().create("set_" + name.toLowerCase() + "_" + slot.name().toLowerCase()), test -> {
                if (ArmorSlot.getSlot(test) != ArmorSlot.getSlot(itemStack)) {
                    return false;
                }
                if (ArmorUtils.isAdvanced(itemStack)) {
                    return false;
                }
                if (ArmorUtils.getSetOnItem(test) == null) {
                    return false;
                }

                return Objects.equals(this, ArmorUtils.getSetOnItem(test));
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
