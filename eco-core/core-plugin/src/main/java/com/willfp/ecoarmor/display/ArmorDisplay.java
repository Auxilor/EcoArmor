package com.willfp.ecoarmor.display;

import com.willfp.ecoarmor.EcoArmorPlugin;
import com.willfp.ecoarmor.config.EcoArmorConfigs;
import com.willfp.ecoarmor.proxy.proxies.SkullProxy;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.meta.ArmorSlot;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import com.willfp.ecoarmor.upgrades.crystal.UpgradeCrystal;
import com.willfp.ecoarmor.util.ProxyUtils;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ArmorDisplay {
    /**
     * Instance of EcoArmor.
     */
    private static final EcoArmorPlugin PLUGIN = EcoArmorPlugin.getInstance();

    /**
     * The prefix for all EcoArmor lines to have in lore.
     */
    public static final String PREFIX = "§v";

    /**
     * Revert display.
     *
     * @param item The item to revert.
     * @return The item, updated.
     */
    public static ItemStack revertDisplay(@Nullable final ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return item;
        }

        ItemMeta meta = item.getItemMeta();
        List<String> itemLore;

        if (meta.hasLore()) {
            itemLore = meta.getLore();
        } else {
            itemLore = new ArrayList<>();
        }

        if (itemLore == null) {
            itemLore = new ArrayList<>();
        }

        itemLore.removeIf(s -> s.startsWith(PREFIX));

        meta.setLore(itemLore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Show itemStack lore, set display name, color, texture, etc.
     *
     * @param itemStack The itemStack to update.
     * @return The itemStack, updated.
     */
    public static ItemStack display(@Nullable final ItemStack itemStack) {
        if (itemStack == null || itemStack.getItemMeta() == null) {
            return itemStack;
        }

        revertDisplay(itemStack);

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return itemStack;
        }

        ArmorSet set = ArmorUtils.getSetOnItem(itemStack);

        if (set == null) {
            String crystalTier = ArmorUtils.getCrystalTier(itemStack);
            UpgradeCrystal crystal = UpgradeCrystal.getByName(crystalTier);

            if (crystalTier != null && crystal != null) {
                meta.setLore(UpgradeCrystal.getByName(crystalTier).getItemStack().getItemMeta().getLore());
                itemStack.setItemMeta(meta);
            }

            ArmorSet shardSet = ArmorUtils.getShardSet(itemStack);

            if (shardSet != null) {
                itemStack.setItemMeta(shardSet.getAdvancementShardItem().getItemMeta());
            }

            return itemStack;
        }

        ArmorSlot slot = ArmorSlot.getSlot(itemStack);
        if (slot == null) {
            return itemStack;
        }

        ItemStack slotStack;

        if (ArmorUtils.isAdvanced(itemStack)) {
            slotStack = set.getAdvancedItemStack(slot);
        } else {
            slotStack = set.getItemStack(slot);
        }
        ItemMeta slotMeta = slotStack.getItemMeta();
        assert slotMeta != null;

        String tier = ArmorUtils.getTier(itemStack);

        List<String> lore = new ArrayList<>();

        for (String s : slotMeta.getLore()) {
            lore.add(s.replace("%tier%", EcoArmorConfigs.TIERS.getString(tier + ".display")));
        }

        meta.setLore(lore);
        meta.setDisplayName(slotMeta.getDisplayName());

        if (meta instanceof SkullMeta && slotMeta instanceof SkullMeta) {
            String base64 = EcoArmorConfigs.SETS.getString(set.getName() + "." + slot.name().toLowerCase() + ".skull-texture");
            ProxyUtils.getProxy(SkullProxy.class).setTexture((SkullMeta) meta, base64);
        }

        if (meta instanceof LeatherArmorMeta && slotMeta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(((LeatherArmorMeta) slotMeta).getColor());
        }

        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
