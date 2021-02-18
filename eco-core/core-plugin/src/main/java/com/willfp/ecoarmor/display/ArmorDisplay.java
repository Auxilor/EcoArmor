package com.willfp.ecoarmor.display;

import com.willfp.eco.util.SkullUtils;
import com.willfp.eco.util.display.DisplayModule;
import com.willfp.eco.util.display.DisplayPriority;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.meta.ArmorSlot;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import com.willfp.ecoarmor.upgrades.tier.Tier;
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
    public ArmorDisplay(@NotNull final AbstractEcoPlugin plugin) {
        super(plugin, DisplayPriority.LOWEST);
    }

    @Override
    protected void display(@NotNull final ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        ArmorSet set = ArmorUtils.getSetOnItem(itemStack);

        if (set == null) {
            String crystalTier = ArmorUtils.getCrystalTier(itemStack);
            Tier crystal = Tier.getByName(crystalTier);

            if (crystalTier != null && crystal != null) {
                meta.setLore(Tier.getByName(crystalTier).getCrystal().getItemMeta().getLore());
                itemStack.setItemMeta(meta);
            }

            ArmorSet shardSet = ArmorUtils.getShardSet(itemStack);

            if (shardSet != null) {
                itemStack.setItemMeta(shardSet.getAdvancementShardItem().getItemMeta());
            }

            return;
        }

        ArmorSlot slot = ArmorSlot.getSlot(itemStack);
        if (slot == null) {
            return;
        }

        ItemStack slotStack;

        if (ArmorUtils.isAdvanced(itemStack)) {
            slotStack = set.getAdvancedItemStack(slot);
        } else {
            slotStack = set.getItemStack(slot);
        }
        ItemMeta slotMeta = slotStack.getItemMeta();
        assert slotMeta != null;

        String tierName = ArmorUtils.getTierName(itemStack);

        Tier tier = Tier.getByName(tierName);

        List<String> lore = new ArrayList<>();

        for (String s : slotMeta.getLore()) {
            if (tierName.equals("default")) {
                s = s.replace("%tier%", this.getPlugin().getConfigYml().getString("default-tier-display"));
            } else {
                s = s.replace("%tier%", tier.getDisplayName());
            }
            lore.add(s);
        }

        meta.setLore(lore);
        meta.setDisplayName(slotMeta.getDisplayName());

        if (meta instanceof SkullMeta && slotMeta instanceof SkullMeta) {
            String base64 = set.getConfig().getString(slot.name().toLowerCase() + ".skull-texture");
            SkullUtils.setSkullTexture((SkullMeta) meta, base64);
        }

        if (meta instanceof LeatherArmorMeta && slotMeta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(((LeatherArmorMeta) slotMeta).getColor());
        }

        itemStack.setItemMeta(meta);
    }

    @Override
    protected void revert(@NotNull final ItemStack itemStack) {

    }
}
