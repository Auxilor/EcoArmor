package com.willfp.ecoarmor.effects.effects;

import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BonusHearts extends Effect<Integer> {
    public BonusHearts() {
        super("bonus-hearts", Integer.class);
    }

    @Override
    protected void onEnable(@NotNull final Player player) {
        AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert maxHealth != null;

        Integer bonus = ArmorUtils.getEffectStrength(player, this);

        if (bonus == null) {
            return;
        }

        maxHealth.removeModifier(new AttributeModifier(this.getUuid(), "bonus-hearts", 0, AttributeModifier.Operation.ADD_NUMBER));
    }

    @Override
    protected void onDisable(@NotNull final Player player) {
        AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert maxHealth != null;

        AttributeModifier modifier = new AttributeModifier(this.getUuid(), "bonus-hearts", 0, AttributeModifier.Operation.ADD_NUMBER);
        if (!maxHealth.getModifiers().contains(modifier)) {
            maxHealth.addModifier(modifier);
        }
    }
}
