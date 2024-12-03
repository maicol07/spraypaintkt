package it.maicol07.spraypaintkt_annotation

/**
 * Marks a property as an attribute of a JSON:API resource.
 *
 * @param name The name of the attribute.
 * @param mutable If the relationship should be editable (can add/edit/remove elements).
 * @param autoTransform If the attribute name should be automatically transformed to snake case when serializing and vice versa when deserializing.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class Attr(val name: String = "", val mutable: Boolean = true, val autoTransform: Boolean = true)
