package com.willfp.ecoarmor.util

class NotNullMap<K, V>(private val handle: MutableMap<K, V>) : MutableMap<K, V> by handle {
    override fun get(key: K): V {
        return handle[key]!!
    }
}