package it.maicol07.spraypaintkt.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("ktPropBy")
open class MapDelegate<R, T>(val map: MutableMap<String, Any?>, val name: String, val defaultValue: T): ReadWriteProperty<R, T> {
    override fun getValue(thisRef: R, property: KProperty<*>): T {
        if (!map.containsKey(name)) return defaultValue
        @Suppress("UNCHECKED_CAST")
        return map[name] as T
    }

    override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        map[name] = value as Any
    }
}