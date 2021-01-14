package com.willfp.ecoarmor.sets.util;

import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.ArmorSets;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ArmorUtils {
    /**
     * Instance of EcoArmor.
     */
    private static final AbstractEcoPlugin PLUGIN = AbstractEcoPlugin.getInstance();

    /**
     * Get armor set that player is wearing.
     *
     * @param player The player to check.
     * @return The set, or null if no full set is worn.
     */
    @Nullable
    public ArmorSet getSetOnPlayer(@NotNull final Player player) {
        List<ArmorSet> found = new ArrayList<>();

        for (ItemStack itemStack : player.getInventory().getArmorContents()) {
            if (itemStack == null) {
                continue;
            }

            ItemMeta meta = itemStack.getItemMeta();

            if (meta == null) {
                continue;
            }

            PersistentDataContainer container = meta.getPersistentDataContainer();
            String setName = container.get(PLUGIN.getNamespacedKeyFactory().create("set"), PersistentDataType.STRING);

            if (setName == null) {
                continue;
            }

            ArmorSet set = ArmorSets.getByName(setName);
            found.add(set);
        }

        if (found.isEmpty()) {
            return null;
        }

        boolean allEqual = true;
        for (ArmorSet set : found) {
            if (!set.equals(found.get(0))) {
                allEqual = false;
                break;
            }
        }

        if (allEqual) {
            return found.get(0);
        }

        return null;
    }

    /**
     * Get the strength of an effect on a player's set.
     *
     * @param player The player to test.
     * @param effect The effect to test.
     * @return The strength, or 0 if not found.
     */
    public double getEffectStrength(@NotNull final Player player,
                                    @NotNull final Effect effect) {
        ArmorSet set = getSetOnPlayer(player);
        if (set == null) {
            return 0;
        }

        if (set.getEffects().containsKey(effect)) {
            return set.getEffects().get(effect);
        }

        return 0;
    }

    /**
     * If a player has an active effect.
     *
     * @param player The player to test.
     * @param effect The effect to test.
     * @return If a player has an active effect.
     */
    public boolean hasEffect(@NotNull final Player player,
                             @NotNull final Effect effect) {
        return getEffectStrength(player, effect) != 0;
    }
}
