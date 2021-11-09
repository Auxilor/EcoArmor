package com.willfp.ecoarmor.display;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.display.DisplayModule;
import com.willfp.eco.core.display.DisplayPriority;
import com.willfp.eco.core.fast.FastItemStack;
import com.willfp.eco.util.SkullUtils;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.meta.ArmorSlot;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import com.willfp.ecoarmor.upgrades.Tier;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ArmorDisplay extends DisplayModule {
    /**
     * Create armor display.
     *
     * @param plugin Instance of EcoArmor.
     */
    public ArmorDisplay(@NotNull final EcoPlugin plugin) {
        super(plugin, DisplayPriority.LOWEST);
    }

    @Override
    public void display(@NotNull final ItemStack itemStack,
                        @NotNull final Object... args) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }
        FastItemStack fis = FastItemStack.wrap(itemStack);

        ArmorSet set = ArmorUtils.getSetOnItem(meta);

        if (set == null) {
            Tier crystalTier = ArmorUtils.getCrystalTier(meta);

            if (crystalTier != null) {
                List<String> lore = fis.getLore();
                lore.addAll(FastItemStack.wrap(crystalTier.getCrystal()).getLore());
                fis.setLore(lore);
            }

            ArmorSet shardSet = ArmorUtils.getShardSet(meta);

            if (shardSet != null) {
                List<String> lore = fis.getLore();
                lore.addAll(FastItemStack.wrap(shardSet.getAdvancementShardItem()).getLore());

                itemStack.setItemMeta(shardSet.getAdvancementShardItem().getItemMeta());
                FastItemStack.wrap(itemStack).setLore(lore);
            }

            return;
        }

        ArmorSlot slot = ArmorSlot.getSlot(itemStack);
        if (slot == null) {
            return;
        }

        ItemStack slotStack;

        if (ArmorUtils.isAdvanced(meta)) {
            slotStack = set.getAdvancedItemStack(slot);
        } else {
            slotStack = set.getItemStack(slot);
        }
        ItemMeta slotMeta = slotStack.getItemMeta();
        assert slotMeta != null;

        Tier tier = ArmorUtils.getTier(meta);

        List<String> lore = new ArrayList<>();

        for (String s : FastItemStack.wrap(slotStack).getLore()) {
            s = s.replace("%tier%", tier.getDisplayName());
            lore.add(s);
        }

        if (meta.hasLore()) {
            lore.addAll(fis.getLore());
        }
        meta.setDisplayName(slotMeta.getDisplayName());

        if (meta instanceof SkullMeta && slotMeta instanceof SkullMeta) {
            if (set.getSkullBase64() != null) {
                SkullUtils.setSkullTexture((SkullMeta) meta, set.getSkullBase64());
            }
        }

        if (meta instanceof LeatherArmorMeta && slotMeta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(((LeatherArmorMeta) slotMeta).getColor());
        }

        itemStack.setItemMeta(meta);
        FastItemStack.wrap(itemStack).setLore(lore);
    }
}
