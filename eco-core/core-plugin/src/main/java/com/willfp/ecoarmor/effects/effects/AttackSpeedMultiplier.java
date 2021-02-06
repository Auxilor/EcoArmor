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

public class AttackSpeedMultiplier extends Effect<Double> {
    public AttackSpeedMultiplier() {
        super("attack-speed-multiplier", Double.class);
    }

    @EventHandler
    public void listener(@NotNull final ArmorEquipEvent event) {
        Player player = event.getPlayer();

        AttributeInstance movementSpeed = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        assert movementSpeed != null;

        this.getPlugin().getScheduler().runLater(() -> {
            Double multiplier = ArmorUtils.getEffectStrength(player, this);
            if (multiplier == null) {
                movementSpeed.removeModifier(new AttributeModifier(this.getUuid(), "attack-speed-multiplier", 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
            } else {
                AttributeModifier modifier = new AttributeModifier(this.getUuid(), "attack-speed-multiplier", multiplier - 1, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
                if (!movementSpeed.getModifiers().contains(modifier)) {
                    movementSpeed.addModifier(modifier);
                }
            }
        }, 1);
    }
}
