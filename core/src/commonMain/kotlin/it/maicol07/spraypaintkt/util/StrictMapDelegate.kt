package it.maicol07.spraypaintkt.util

import kotlin.reflect.KProperty

@Suppress("ktPropBy")
class StrictMapDelegate<R, T>(map: MutableMap<String, Any?>, name: String): MapDelegate<R, T?>(map, name, null) {
    override fun getValue(thisRef: R, property: KProperty<*>): T {
        @Suppress("UNCHECKED_CAST")
        return map[name] as T
    }
}