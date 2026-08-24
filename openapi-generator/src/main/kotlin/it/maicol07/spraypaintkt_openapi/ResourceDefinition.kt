package it.maicol07.spraypaintkt_openapi

import com.squareup.kotlinpoet.TypeName

/**
 * A JSON:API resource object recovered from an OpenAPI document.
 *
 * @param resourceType The literal JSON:API `type` value.
 * @param endpoint The collection endpoint, relative to the API base URL.
 * @param className The generated schema class name, without the `Schema` suffix.
 */
data class ResourceDefinition(
    val resourceType: String,
    val endpoint: String,
    val className: String,
    val attributes: List<AttributeDefinition>,
    val relations: List<RelationDefinition>,
)

/**
 * An entry of a resource object's `attributes` member.
 *
 * @param jsonName The attribute name as it appears on the wire.
 * @param propertyName The Kotlin property name.
 * @param type The non-null base type. Ignored when [enum] is set, since that class lives in the
 *   package only the emitter knows.
 * @param mutable `false` for members the document marks `readOnly`.
 * @param enum The enum class to emit alongside the schema, when the member is a closed set of names.
 */
data class AttributeDefinition(
    val jsonName: String,
    val propertyName: String,
    val type: TypeName,
    val nullable: Boolean,
    val mutable: Boolean,
    val enum: EnumDefinition?,
)

/**
 * A closed set of string values, emitted as a Kotlin enum next to the schema that uses it.
 *
 * Constant names are the wire values verbatim: the KSP processor round-trips attributes through
 * `valueOf(...)` and `Enum.name`.
 */
data class EnumDefinition(val className: String, val values: List<String>)

/**
 * An entry of a resource object's `relationships` member.
 *
 * @param targetResourceType The literal JSON:API `type` of the linked resource.
 * @param toMany Whether the linkage is an array. Never inferred from the relationship name.
 * @param nullable Whether a to-one linkage may be absent. Always `false` for to-many.
 */
data class RelationDefinition(
    val jsonName: String,
    val propertyName: String,
    val targetResourceType: String,
    val toMany: Boolean,
    val nullable: Boolean,
)

/**
 * How a document encodes the JSON:API resource type.
 *
 * There is no standard OpenAPI description of JSON:API, so the encoding has to be detected.
 */
enum class OpenApiDialect {
    /** `type` is pinned to a single `enum`/`const` value, as emitted by jsonapi.rb, Elide and Drupal. */
    PINNED_TYPE,

    /** `type` is a free-form string and the resource type is the component name, as emitted by API Platform. */
    COMPONENT_NAME,
}

/** Raised when the OpenAPI document cannot be read as a JSON:API description. */
class OpenApiSchemaException(message: String, cause: Throwable? = null) : IllegalArgumentException(message, cause)
