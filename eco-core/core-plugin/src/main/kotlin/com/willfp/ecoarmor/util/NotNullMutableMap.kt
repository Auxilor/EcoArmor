package com.willfp.ecoarmor.util

/**
 * A mutable map whose [get] throws if the key is missing.
 */
class NotNullMutableMap<K, V>(private val handle: MutableMap<K, V>) : MutableMap<K, V> by handle {
    override fun get(key: K): V = handle.getValue(key)
}

fun <K, V> notNullMutableMapOf(): NotNullMutableMap<K, V> = NotNullMutableMap(mutableMapOf())
