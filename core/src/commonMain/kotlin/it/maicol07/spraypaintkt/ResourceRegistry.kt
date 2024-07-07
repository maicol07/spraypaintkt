package it.maicol07.spraypaintkt

import kotlin.reflect.KClass

object ResourceRegistry {
    val resources = mutableMapOf<KClass<out Resource>, Resource.Companion<out Resource>>()

    inline fun <reified R: Resource> registerResource(
        resourceCompanion: Resource.Companion<R>,
        update: Boolean = true
    ) {
        if (!update && resources.containsKey(R::class)) throw IllegalArgumentException("Resource ${R::class} already registered")
        resources[R::class] = resourceCompanion
    }

    inline fun <reified R: Resource> createInstance(): R {
        return createInstance(R::class)
    }

    fun <R: Resource> createInstance(clazz: KClass<R>): R {
        val resource = resources[clazz]?.factory?.invoke() ?: throw IllegalArgumentException("$clazz is not a registered resource")
        if (resource::class == clazz) {
            @Suppress("UNCHECKED_CAST")
            return resource as R
        }
        throw IllegalArgumentException("Factory for class $clazz returned an instance of ${resource::class}")
    }

    fun createInstance(type: String): Resource =
        resources.entries.firstOrNull { it.value.resourceType == type }?.value?.factory?.invoke() ?: throw IllegalArgumentException("No registered resource found for type $type")

    operator fun <R: Resource> get(clazz: KClass<R>): Resource.Companion<R> {
        @Suppress("UNCHECKED_CAST")
        return resources[clazz] as Resource.Companion<R>? ?: throw IllegalArgumentException("No registered resource found for class $clazz")
    }

    inline fun <reified R: Resource> get(): Resource.Companion<R> = get(R::class)

    operator fun get(type: String): Resource.Companion<out Resource> =
        resources.entries.firstOrNull { it.value.resourceType == type }?.value ?: throw IllegalArgumentException("No registered resource found for type $type")

    operator fun get(companion: Resource.Companion<out Resource>): KClass<out Resource> =
        resources.entries.firstOrNull { it.value == companion }?.key ?: throw IllegalArgumentException("No registered resource found for companion $companion")

    fun getEntry(type: String): Pair<KClass<out Resource>, Resource.Companion<out Resource>> =
        resources.entries.firstOrNull { it.value.resourceType == type }?.toPair() ?: throw IllegalArgumentException("No registered resource found for type $type")
}