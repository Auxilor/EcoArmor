package com.willfp.ecoarmor.api.event

import com.willfp.ecoarmor.sets.ArmorSet

interface ArmorSetEvent {
    val set: ArmorSet
    val advanced: Boolean
}