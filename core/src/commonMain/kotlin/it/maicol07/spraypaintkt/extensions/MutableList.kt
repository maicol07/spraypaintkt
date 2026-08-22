package it.maicol07.spraypaintkt.extensions

/** A mutable list that tracks every mutation, including iterator and sub-list changes. */
@Suppress("unused")
class DirtyList<T>(
    private val delegate: MutableList<T>,
    private val callback: (item: T, list: DirtyList<T>) -> Unit = { _, _ -> },
) : AbstractMutableList<T>() {
    private val changes = mutableListOf<T>()

    override val size: Int
        get() = delegate.size

    override fun get(index: Int): T = delegate[index]

    override fun add(index: Int, element: T) {
        delegate.add(index, element)
        trackChange(element)
    }

    override fun removeAt(index: Int): T = delegate.removeAt(index).also(::trackChange)

    override fun set(index: Int, element: T): T = delegate.set(index, element).also {
        trackChange(element)
    }

    private fun trackChange(element: T) {
        changes.add(element)
        callback(element, this)
    }

    fun getChanges(): List<T> = changes.toList()

    fun clearChanges() {
        changes.clear()
    }
}

/** Track changes in a mutable list. */
fun <T> MutableList<T>.trackChanges(
    callback: (item: T, list: DirtyList<T>) -> Unit = { _, _ -> },
): DirtyList<T> = DirtyList(this, callback)
