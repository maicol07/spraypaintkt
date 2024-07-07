package it.maicol07.spraypaintkt.extensions

import it.maicol07.spraypaintkt.Resource
import it.maicol07.spraypaintkt.util.MapDelegate
import it.maicol07.spraypaintkt.util.StrictMapDelegate

/**
 * Delegate for relationships. Let you specify the name of the relationship to delegate to.
 *
 * @param name The name of the relationship.
 * @param R The type of the resource.
 * @param RL The type of the relationship.
 */
fun <R : Resource, RL : Resource> R.hasOneRelationship(name: String) = StrictMapDelegate<R, RL>(relationships, name)
/**
 * Delegate for relationships. Let you specify the name of the relationship to delegate to.
 *
 * @param name The name of the relationship.
 * @param defaultValue The default value of the relationship.
 * @param R The type of the resource.
 * @param RL The type of the relationship.
 */
fun <R : Resource, RL : Resource> R.hasOneRelationship(name: String, defaultValue: RL) = MapDelegate<R, RL>(relationships, name, defaultValue)

/**
 * Delegate for relationships. Let you specify the name of the relationship to delegate to.
 *
 * @param name The name of the relationship.
 * @param defaultValue The default value of the relationship.
 * @param R The type of the resource.
 * @param RL The type of the relationship.
 */
fun <R : Resource, RL : Resource> R.nullableHasOneRelationship(name: String, defaultValue: RL? = null) = MapDelegate<R, RL?>(relationships, name, defaultValue)

/**
 * Delegate for has-many relationships. Let you specify the name of the relationship to delegate to.
 *
 * @param name The name of the relationship.
 * @param R The type of the resource.
 * @param RL The type of the relationship.
 */
fun <R : Resource, RL : Resource> R.hasManyRelationship(name: String) = MapDelegate<R, List<RL>>(relationships, name, emptyList())

/**
 * Delegate for has-many relationships. Let you specify the name of the relationship to delegate to.
 *
 * @param name The name of the relationship.
 * @param defaultValue The default value of the relationship.
 * @param R The type of the resource.
 * @param RL The type of the relationship.
 */
fun <R : Resource, RL : Resource> R.hasManyRelationship(name: String, defaultValue: List<RL>? = emptyList()) = MapDelegate<R, List<RL>?>(relationships, name, defaultValue)