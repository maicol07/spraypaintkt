package it.maicol07.spraypaintkt

import kotlin.reflect.KClass

/**
 * A JSON:API resource.
 */
object ResourceRegistry {
    /** The registered resources. */
    val resources = mutableMapOf<KClass<out Resource>, Resource.Companion<out Resource>>()

    /**
     * Registers a resource.
     *
     * @param resourceCompanion The companion object of the resource.
     * @param update Whether to update the resource if it already exists.
     * @param R The type of the resource.
     */
    inline fun <reified R: Resource> registerResource(
        resourceCompanion: Resource.Companion<R>,
        update: Boolean = true
    ) {
        if (!update && resources.containsKey(R::class)) throw IllegalArgumentException("Resource ${R::class} already registered")
        resources[R::class] = resourceCompanion
    }

    /**
     * Registers a resource.
     *
     * @param R The type of the resource.
     *
     * @return The companion object of the resource.
     */
    inline fun <reified R: Resource> createInstance(): R {
        return createInstance(R::class)
    }

    /**
     * Creates a new instance of a resource.
     *
     * @param clazz The class of the resource.
     * @param R The type of the resource.
     * @return The new instance.
     */
    fun <R: Resource> createInstance(clazz: KClass<R>): R {
        val resource = resources[clazz]?.factory?.invoke() ?: throw IllegalArgumentException("$clazz is not a registered resource")
        if (resource::class == clazz) {
            @Suppress("UNCHECKED_CAST")
            return resource as R
        }
        throw IllegalArgumentException("Factory for class $clazz returned an instance of ${resource::class}")
    }

    /**
     * Creates a new instance of a resource.
     *
     * @param type The type of the resource.
     *
     * @return The new instance.
     */
    fun createInstance(type: String): Resource =
        resources.entries.firstOrNull { it.value.resourceType == type }?.value?.factory?.invoke() ?: throw IllegalArgumentException("No registered resource found for type $type")

    /**
     * Gets a resource companion.
     *
     * @param clazz The class of the resource.
     * @param R The type of the resource.
     *
     * @return The companion object of the resource.
     */
    operator fun <R: Resource> get(clazz: KClass<R>): Resource.Companion<R> {
        @Suppress("UNCHECKED_CAST")
        return resources[clazz] as Resource.Companion<R>? ?: throw IllegalArgumentException("No registered resource found for class $clazz")
    }

    /**
     * Gets a resource companion.
     *
     * @param R The type of the resource.
     */
    inline fun <reified R: Resource> get(): Resource.Companion<R> = get(R::class)

    /**
     * Gets a resource companion.
     *
     * @param type The type of the resource.
     *
     * @return The companion object of the resource.
     */
    operator fun get(type: String): Resource.Companion<out Resource> =
        resources.entries.firstOrNull { it.value.resourceType == type }?.value ?: throw IllegalArgumentException("No registered resource found for type $type")

    /**
     * Gets a resource class.
     *
     * @param companion The companion object of the resource.
     *
     * @return The class of the resource.
     */
    operator fun get(companion: Resource.Companion<out Resource>): KClass<out Resource> =
        resources.entries.firstOrNull { it.value == companion }?.key ?: throw IllegalArgumentException("No registered resource found for companion $companion")

    /**
     * Gets a resource class.
     *
     * @param type The type of the resource.
     *
     * @return The class of the resource.
     */
    fun getEntry(type: String): Pair<KClass<out Resource>, Resource.Companion<out Resource>> =
        resources.entries.firstOrNull { it.value.resourceType == type }?.toPair() ?: throw IllegalArgumentException("No registered resource found for type $type")
}