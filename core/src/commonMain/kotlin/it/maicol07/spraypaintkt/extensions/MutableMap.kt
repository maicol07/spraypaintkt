package it.maicol07.spraypaintkt.extensions

class DirtyMap<K, V>(private val delegate: MutableMap<K, V>) : MutableMap<K, V> by delegate {
    private var changes: MutableMap<K,V>? = null

    override fun put(key: K, value: V): V? {
        val oldValue = delegate.put(key, value)
        trackChange(key, oldValue, value)
        return oldValue
    }

    override fun putAll(from: Map<out K, V>) {
        delegate.putAll(from)
        from.forEach { (key, value) -> trackChange(key, null, value) }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun trackChange(key: K, oldValue: V?, newValue: V) {
        if (changes == null) {
            changes = mutableMapOf()
        }
        changes!![key] = newValue
    }

    fun getChanges(): Map<K, V> {
        return changes ?: emptyMap()
    }

    fun clearChanges() {
        changes = null
    }
}

fun <K, V> MutableMap<K, V>.trackChanges(): DirtyMap<K, V> {
    return DirtyMap(this)
}