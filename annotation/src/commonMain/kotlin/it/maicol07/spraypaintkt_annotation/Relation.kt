package it.maicol07.spraypaintkt_annotation

/**
 * Annotates a class representing a to-many relationship in a JSON:API resource.
 *
 * @param name The name of the relationship.
 * @param mutable If the relationship should be editable (can add/edit/remove elements).
 * @param autoTransform If the relationship name should be automatically transformed to snake case when serializing and vice versa when deserializing.
 */
@Target(AnnotationTarget.PROPERTY)
@Repeatable
annotation class Relation(val name: String = "", val mutable: Boolean = true, val autoTransform: Boolean = true)