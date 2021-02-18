package com.willfp.ecoarmor.upgrades.tier;

import lombok.Data;

@Data
public class TierProperties {
    /**
     * Armor property.
     */
    private final int armor;

    /**
     * Armor toughness property.
     */
    private final int toughness;

    /**
     * Knockback resistance property.
     */
    private final int knockback;

    /**
     * Speed percentage.
     */
    private final int speed;

    /**
     * Attack speed percentage.
     */
    private final int attackSpeed;

    /**
     * Attack damage percentage.
     */
    private final int attackDamage;

    /**
     * Attack knockback percentage.
     */
    private final int attackKnockback;
}
