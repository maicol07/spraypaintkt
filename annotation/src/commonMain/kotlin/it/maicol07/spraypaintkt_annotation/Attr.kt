package it.maicol07.spraypaintkt_annotation

/**
 * Marks a property as an attribute of a JSON:API resource.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class Attr(val name: String = "", val autoTransform: Boolean = true)
