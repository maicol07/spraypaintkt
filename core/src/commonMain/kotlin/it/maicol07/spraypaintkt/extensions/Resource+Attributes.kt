package it.maicol07.spraypaintkt.extensions

import it.maicol07.spraypaintkt.Resource
import it.maicol07.spraypaintkt.util.MapDelegate
import it.maicol07.spraypaintkt.util.StrictMapDelegate


/**
 * Delegate for attributes. Let you specify the name of the attribute to delegate to.
 *
 * @param name The name of the attribute.
 * @param R The type of the resource.
 * @param T The type of the attribute.
 * @return The delegate.
 */
fun <R : Resource, T> R.attribute(name: String) = StrictMapDelegate<R, T>(attributes, name)

/**
 * Delegate for attributes. Let you specify the name of the attribute to delegate to.
 *
 * @param name The name of the attribute.
 * @param defaultValue The default value of the attribute.
 * @param R The type of the resource.
 * @param T The type of the attribute.
 * @return The delegate.
 */
fun <R : Resource, T> R.attribute(name: String, defaultValue: T) = MapDelegate<R, T>(attributes, name, defaultValue)

/**
 * Delegate for nullable attributes. Let you specify the name of the attribute to delegate to.
 *
 * @param name The name of the attribute.
 * @param defaultValue The default value of the attribute.
 * @param R The type of the resource.
 * @param T The type of the attribute.
 * @return The delegate.
 */
fun <R : Resource, T> R.nullableAttribute(name: String, defaultValue: T? = null) = MapDelegate<R, T?>(attributes, name, defaultValue)
