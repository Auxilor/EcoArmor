package com.willfp.ecoarmor.effects;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.willfp.ecoarmor.effects.effects.AttackSpeedMultiplier;
import com.willfp.ecoarmor.effects.effects.BonusHearts;
import com.willfp.ecoarmor.effects.effects.BossDamageMultiplier;
import com.willfp.ecoarmor.effects.effects.BowDamageMultiplier;
import com.willfp.ecoarmor.effects.effects.DamageMultiplier;
import com.willfp.ecoarmor.effects.effects.DamageTakenMultiplier;
import com.willfp.ecoarmor.effects.effects.EvadeChance;
import com.willfp.ecoarmor.effects.effects.ExperienceMultiplier;
import com.willfp.ecoarmor.effects.effects.FallDamageMultiplier;
import com.willfp.ecoarmor.effects.effects.Flight;
import com.willfp.ecoarmor.effects.effects.HungerLossMultiplier;
import com.willfp.ecoarmor.effects.effects.KnockbackMultiplier;
import com.willfp.ecoarmor.effects.effects.MeleeDamageMultiplier;
import com.willfp.ecoarmor.effects.effects.RegenerationMultiplier;
import com.willfp.ecoarmor.effects.effects.SpeedMultiplier;
import com.willfp.ecoarmor.effects.effects.TridentDamageMultiplier;
import com.willfp.ecoarmor.effects.effects.WarpChance;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@UtilityClass
@SuppressWarnings("unused")
public class Effects {
    /**
     * All registered effects.
     */
    private static final BiMap<String, Effect<?>> BY_NAME = HashBiMap.create();

    public static final Effect<?> BOW_DAMAGE_MULTIPLIER = new BowDamageMultiplier();
    public static final Effect<?> DAMAGE_MULTIPLIER = new DamageMultiplier();
    public static final Effect<?> DAMAGE_TAKEN_MULTIPLIER = new DamageTakenMultiplier();
    public static final Effect<?> EVADE_CHANCE = new EvadeChance();
    public static final Effect<?> FALL_DAMAGE_MULTIPLIER = new FallDamageMultiplier();
    public static final Effect<?> MELEE_DAMAGE_MULTIPLIER = new MeleeDamageMultiplier();
    public static final Effect<?> TRIDENT_DAMAGE_MULTIPLIER = new TridentDamageMultiplier();
    public static final Effect<?> BONUS_HEARTS = new BonusHearts();
    public static final Effect<?> SPEED_MULTIPLIER = new SpeedMultiplier();
    public static final Effect<?> EXPERIENCE_MULTIPLIER = new ExperienceMultiplier();
    public static final Effect<?> REGENERATION_MULTIPLIER = new RegenerationMultiplier();
    public static final Effect<?> WARP_CHANCE = new WarpChance();
    public static final Effect<?> ATTACK_SPEED_MULTIPLIER = new AttackSpeedMultiplier();
    public static final Effect<?> FLIGHT = new Flight();
    public static final Effect<?> HUNGER_LOSS_MULTIPLIER = new HungerLossMultiplier();
    public static final Effect<?> BOSS_DAMAGE_MULTIPLIER = new BossDamageMultiplier();
    public static final Effect<?> KNOCKBACK_MULTIPLIER = new KnockbackMultiplier();

    /**
     * Get effect matching name.
     *
     * @param name The name to query.
     * @return The matching effect, or null if not found.
     */
    @Nullable
    public static Effect<?> getByName(@NotNull final String name) {
        return BY_NAME.get(name);
    }

    /**
     * List of all registered effects.
     *
     * @return The effects.
     */
    public static List<Effect<?>> values() {
        return ImmutableList.copyOf(BY_NAME.values());
    }

    /**
     * Add new effect to EcoArmor.
     *
     * @param effect The effect to add.
     */
    public static void addNewEffect(@NotNull final Effect<?> effect) {
        BY_NAME.remove(effect.getName());
        BY_NAME.put(effect.getName(), effect);
    }
}
