package it.maicol07.spraypaintkt.extensions

/** A mutable map that tracks every mutation, including mutations through its views. */
class DirtyMap<K, V>(
    private val delegate: MutableMap<K, V>,
    private val callback: (key: K, value: V, map: DirtyMap<K, V>) -> Unit = { _, _, _ -> },
) : AbstractMutableMap<K, V>() {
    private val changes = mutableMapOf<K, V?>()

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> =
        object : AbstractMutableSet<MutableMap.MutableEntry<K, V>>() {
            override val size: Int
                get() = delegate.size

            override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> {
                val iterator = delegate.entries.iterator()
                var current: MutableMap.MutableEntry<K, V>? = null
                return object : MutableIterator<MutableMap.MutableEntry<K, V>> {
                    override fun hasNext(): Boolean = iterator.hasNext()

                    override fun next(): MutableMap.MutableEntry<K, V> {
                        val entry = iterator.next()
                        current = entry
                        return object : MutableMap.MutableEntry<K, V> {
                            override val key: K = entry.key
                            override val value: V
                                get() = entry.value

                            override fun setValue(newValue: V): V = entry.setValue(newValue).also {
                                trackChange(key, newValue)
                            }

                            override fun equals(other: Any?): Boolean =
                                other is Map.Entry<*, *> && key == other.key && value == other.value

                            override fun hashCode(): Int =
                                (key?.hashCode() ?: 0) xor (value?.hashCode() ?: 0)

                            override fun toString(): String = "$key=$value"
                        }
                    }

                    override fun remove() {
                        val entry = checkNotNull(current) { "next() must be called before remove()" }
                        val key = entry.key
                        val value = entry.value
                        iterator.remove()
                        trackRemoval(key, value)
                        current = null
                    }
                }
            }

            override fun add(element: MutableMap.MutableEntry<K, V>): Boolean =
                throw UnsupportedOperationException("Map entries cannot be added directly")
        }

    override fun put(key: K, value: V): V? = delegate.put(key, value).also {
        trackChange(key, value)
    }

    override fun remove(key: K): V? {
        if (!delegate.containsKey(key)) return null
        return delegate.remove(key).also { oldValue ->
            @Suppress("UNCHECKED_CAST")
            trackRemoval(key, oldValue as V)
        }
    }

    fun trackChange(key: K, @Suppress("UNUSED_PARAMETER") oldValue: V?, newValue: V) {
        trackChange(key, newValue)
    }

    private fun trackChange(key: K, newValue: V) {
        changes[key] = newValue
        callback(key, newValue, this)
    }

    private fun trackRemoval(key: K, oldValue: V) {
        changes[key] = null
        callback(key, oldValue, this)
    }

    fun getChanges(): Map<K, V?> = changes.toMap()

    fun clearChanges() {
        changes.clear()
    }
}

/** Track changes in a mutable map. */
fun <K, V> MutableMap<K, V>.trackChanges(
    callback: (key: K, value: V, map: DirtyMap<K, V>) -> Unit = { _, _, _ -> },
): DirtyMap<K, V> = DirtyMap(this, callback)
