package com.willfp.ecoarmor.effects.effects;

import com.willfp.ecoarmor.effects.Effect;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KnockbackMultiplier extends Effect<Double> {
    public KnockbackMultiplier() {
        super("knockback-multiplier", Double.class);
    }

    @Override
    protected void onEnable(@NotNull final Player player) {
        AttributeInstance movementSpeed = player.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK);
        assert movementSpeed != null;

        Double strength = this.getStrengthForPlayer(player);

        if (strength == null) {
            return;
        }

        AttributeModifier modifier = new AttributeModifier(this.getUuid(), "kb-multiplier", strength - 1, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
        if (movementSpeed.getModifiers().stream().noneMatch(attributeModifier -> attributeModifier.getUniqueId().equals(modifier.getUniqueId()))) {
            movementSpeed.addModifier(modifier);
        }
    }

    @Override
    protected void onDisable(@NotNull final Player player) {
        AttributeInstance movementSpeed = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        assert movementSpeed != null;

        movementSpeed.removeModifier(new AttributeModifier(this.getUuid(), "kb-multiplier", 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
    }
}
