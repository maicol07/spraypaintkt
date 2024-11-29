package it.maicol07.spraypaintkt_annotation

import kotlin.reflect.KClass

/**
 * Annotates a class representing a to-one relationship in a JSON:API resource.
 *
 * @param name The name of the relationship.
 * @param resourceType The type of the related resource.
 * @param canBeNull If the relationship can have an empty value.
 * @param propertyName The name of the property in the class. Defaults to the name of the relationship.
 *
 * @see ToOneRelationship
 * @see ResourceSchema
 */
@Target(AnnotationTarget.CLASS)
@Repeatable
annotation class ToOneRelationship(val name: String, val resourceType: @ResourceSchema(endpoint = "") KClass<*>, val canBeNull: Boolean = false, val propertyName: String = "")
