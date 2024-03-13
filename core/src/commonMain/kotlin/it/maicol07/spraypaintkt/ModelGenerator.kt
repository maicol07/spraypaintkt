package it.maicol07.spraypaintkt

import kotlin.reflect.KClass

/**
 * A model generator for the client.
 */
interface ModelGenerator {
    /**
     * Generate a new instance of the given class.
     *
     * @param clazz The class to generate
     * @return The generated instance
     */
    fun <R: Resource> generate(clazz: KClass<R>): R
}