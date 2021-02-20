package com.willfp.ecoarmor.conditions;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.willfp.ecoarmor.conditions.conditions.ConditionBelowY;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@UtilityClass
@SuppressWarnings("unused")
public class Conditions {
    /**
     * All registered effects.
     */
    private static final BiMap<String, Condition<?>> BY_NAME = HashBiMap.create();

    public static final Condition<?> BELOW_Y = new ConditionBelowY();

    /**
     * Get condition matching name.
     *
     * @param name The name to query.
     * @return The matching condition, or null if not found.
     */
    public static Condition<?> getByName(@NotNull final String name) {
        return BY_NAME.get(name);
    }

    /**
     * List of all registered conditions.
     *
     * @return The conditions.
     */
    public static List<Condition<?>> values() {
        return ImmutableList.copyOf(BY_NAME.values());
    }

    /**
     * Add new condition to EcoArmor.
     *
     * @param condition The condition to add.
     */
    public static void addNewCondition(@NotNull final Condition<?> condition) {
        BY_NAME.remove(condition.getName());
        BY_NAME.put(condition.getName(), condition);
    }
}
