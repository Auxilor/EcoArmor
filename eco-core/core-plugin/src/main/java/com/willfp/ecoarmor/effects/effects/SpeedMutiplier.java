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

public class SpeedMutiplier extends Effect<Double> {
    private static final UUID MODIFIER_UUID = UUID.nameUUIDFromBytes("speed-multiplier".getBytes());

    public SpeedMutiplier() {
        super("speed-multiplier");
    }

    @EventHandler
    public void onArmorEquip(@NotNull final ArmorEquipEvent event) {
        Player player = event.getPlayer();

        AttributeInstance movementSpeed = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        assert movementSpeed != null;

        this.getPlugin().getScheduler().runLater(() -> {
            Double multiplier = ArmorUtils.getEffectStrength(player, this);
            if (multiplier == null) {
                movementSpeed.removeModifier(new AttributeModifier(MODIFIER_UUID, "speed-multiplier", 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
            } else {
                AttributeModifier modifier = new AttributeModifier(MODIFIER_UUID, "speed-multiplier", multiplier - 1, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
                if (!movementSpeed.getModifiers().contains(modifier)) {
                    movementSpeed.addModifier(modifier);
                }
            }
        }, 1);
    }
}
