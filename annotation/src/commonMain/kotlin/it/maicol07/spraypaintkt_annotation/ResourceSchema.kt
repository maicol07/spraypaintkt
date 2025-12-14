package it.maicol07.spraypaintkt_annotation

import it.maicol07.spraypaintkt.interfaces.JsonApiConfig
import kotlin.reflect.KClass

/**
 * Annotates a class as a JSON:API resource schema with the given endpoint.
 *
 * @param resourceType The type of the JSON:API resource. If empty, it will be automatically derived from the class name (lowercase ).
 * @param endpoint The endpoint to use for the resource. If empty, it will be automatically derived from the resource type.
 * @param config The configuration to use for the resource. Defaults to the configuration object that extends [JsonApiConfig] and marked with [DefaultInstance].
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class ResourceSchema(
    val resourceType: String = "",
    val endpoint: String = "",
    val config: KClass<out JsonApiConfig> = JsonApiConfig::class
)
