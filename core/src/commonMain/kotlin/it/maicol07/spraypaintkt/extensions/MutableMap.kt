package it.maicol07.spraypaintkt.extensions

/**
 * A mutable map that tracks changes.
 */
class DirtyMap<K, V>(
    private val delegate: MutableMap<K, V>,
    private val callback: (key: K, value: V, map: DirtyMap<K, V>) -> Unit = { _, _, _ -> }
) : MutableMap<K, V> by delegate {
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

    override fun remove(key: K): V? {
        val oldValue = delegate.remove(key)
        if (oldValue != null) {
            trackRemovalChange(key, oldValue)
        }
        return oldValue
    }

    @Suppress("UNUSED_PARAMETER")
    fun trackChange(key: K, oldValue: V?, newValue: V) {
        if (changes == null) {
            changes = mutableMapOf()
        }
        changes!![key] = newValue
        callback(key, newValue, this)
    }

    /**
     * Track the removal of a key-value pair.
     *
     * @param key The key.
     * @param oldValue The old value.
     */
    fun trackRemovalChange(key: K, oldValue: V) {
        callback(key, oldValue, this)
    }

    /**
     * Get the changes.
     *
     * @return The changes.
     */
    fun getChanges(): Map<K, V> {
        return changes ?: emptyMap()
    }

    /**
     * Clear the changes.
     */
    fun clearChanges() {
        changes = null
    }
}

/**
 * Track changes in a mutable map.
 *
 * @param callback The callback to call when a change is tracked.
 * @return The dirty map.
 */
fun <K, V> MutableMap<K, V>.trackChanges(callback: (key: K, value: V, map: DirtyMap<K, V>) -> Unit = { _, _, _ ->}): DirtyMap<K, V> {
    return DirtyMap(this, callback)
}