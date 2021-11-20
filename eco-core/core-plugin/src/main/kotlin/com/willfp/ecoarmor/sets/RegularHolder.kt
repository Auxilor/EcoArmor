package com.willfp.ecoarmor.sets

import com.willfp.libreforge.Holder
import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.effects.ConfiguredEffect

class RegularHolder(override val conditions: Set<ConfiguredCondition>, override val effects: Set<ConfiguredEffect>) :
    Holder {
}
