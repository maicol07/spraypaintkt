package it.maicol07.spraypaintkt.extensions

class DirtyList<T>(private val delegate: MutableList<T>, private val callback: (item: T, list: DirtyList<T>) -> Unit = {_, _ ->}) : MutableList<T> by delegate {
    private var changes: MutableList<T>? = null

    override fun add(element: T): Boolean {
        val result = delegate.add(element)
        trackChange(element)
        return result
    }

    override fun add(index: Int, element: T) {
        delegate.add(index, element)
        trackChange(element)
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        val result = delegate.addAll(index, elements)
        elements.forEach { trackChange(it) }
        return result
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val result = delegate.addAll(elements)
        elements.forEach { trackChange(it) }
        return result
    }

    override fun clear() {
        delegate.clear()
        changes = null
    }

    override fun remove(element: T): Boolean {
        val result = delegate.remove(element)
        if (result) {
            trackChange(element)
        }
        return result
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        val result = delegate.removeAll(elements)
        elements.forEach { trackChange(it) }
        return result
    }

    override fun iterator(): MutableIterator<T> {
        return object : MutableIterator<T> {
            private val iterator = delegate.iterator()
            private var last: T? = null

            override fun hasNext(): Boolean {
                return iterator.hasNext()
            }

            override fun next(): T {
                last = iterator.next()
                return last!!
            }

            override fun remove() {
                iterator.remove()
                trackChange(last!!)
            }
        }
    }

    override fun removeAt(index: Int): T {
        val element = delegate.removeAt(index)
        trackChange(element)
        return element
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val result = delegate.retainAll(elements)
        changes = delegate.filterNot { elements.contains(it) }.toMutableList()
        return result
    }

    private fun trackChange(element: T) {
        if (changes == null) {
            changes = mutableListOf()
        }
        changes!!.add(element)
        callback(element, this)
    }

    fun getChanges(): List<T> {
        return changes ?: emptyList()
    }

    fun clearChanges() {
        changes = null
    }
}

fun <T> MutableList<T>.trackChanges(callback: (item: T, list: DirtyList<T>) -> Unit = {_, _ ->}): DirtyList<T> {
    return DirtyList(this, callback)
}