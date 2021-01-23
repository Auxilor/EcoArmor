package com.willfp.ecoarmor.effects.effects;

import com.willfp.eco.util.events.armorequip.ArmorEquipEvent;
import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BonusHearts extends Effect<Integer> {
    private static final UUID MODIFIER_UUID = UUID.nameUUIDFromBytes("bonus-hearts".getBytes());

    public BonusHearts() {
        super("bonus-hearts");
    }

    @EventHandler
    public void listener(@NotNull final ArmorEquipEvent event) {
        Player player = event.getPlayer();

        AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert maxHealth != null;

        this.getPlugin().getScheduler().runLater(() -> {
            Integer bonus = ArmorUtils.getEffectStrength(player, this);
            if (bonus == null) {
                maxHealth.removeModifier(new AttributeModifier(MODIFIER_UUID, "bonus-hearts", 0, AttributeModifier.Operation.ADD_NUMBER));
            } else {
                AttributeModifier modifier = new AttributeModifier(MODIFIER_UUID, "bonus-hearts", bonus * 2, AttributeModifier.Operation.ADD_NUMBER);
                if (!maxHealth.getModifiers().contains(modifier)) {
                    maxHealth.addModifier(modifier);
                }
            }
        }, 1);
    }
}
