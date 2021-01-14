package com.willfp.ecoarmor.effects;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.willfp.ecoarmor.effects.effects.*;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@UtilityClass
public class Effects {
    private static final BiMap<String, Effect> BY_NAME = HashBiMap.create();

    public static final Effect BOW_DAMAGE_MULTIPLIER = new BowDamageMultiplier();
    public static final Effect DAMAGE_MULTIPLIER = new DamageMultiplier();
    public static final Effect DAMAGE_TAKEN_MULTIPLIER = new DamageTakenMultiplier();
    public static final Effect EVADE_CHANCE = new EvadeChance();
    public static final Effect FALL_DAMAGE_MULTIPLIER = new FallDamageMultiplier();
    public static final Effect MELEE_DAMAGE_MULTIPLIER = new MeleeDamageMultiplier();
    public static final Effect TRIDENT_DAMAGE_MULTIPLIER = new TridentDamageMultiplier();
    public static final Effect BONUS_HEARTS = new BonusHearts();
    public static final Effect SPEED_MULTIPLIER = new SpeedMutiplier();
    public static final Effect EXPERIENCE_MULTIPLIER = new ExperienceMultiplier();
    public static final Effect REGENERATION_MULTIPLIER = new RegenerationMultiplier();

    public static Effect getByName(@NotNull final String name) {
        return BY_NAME.get(name);
    }

    public static List<Effect> values() {
        return ImmutableList.copyOf(BY_NAME.values());
    }

    /**
     * Add new effect to EcoArmor.
     *
     * @param effect The effect to add.
     */
    public static void addNewEffect(@NotNull final Effect effect) {
        BY_NAME.remove(effect.getName());
        BY_NAME.put(effect.getName(), effect);
    }
}
