package it.maicol07.spraypaintkt_openapi

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.MAP
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.parser.OpenAPIV3Parser
import io.swagger.v3.parser.core.models.ParseOptions
import net.pearx.kasechange.toCamelCase
import net.pearx.kasechange.toPascalCase

private val NULLABLE_ANY = ANY.copy(nullable = true)
private val DYNAMIC_OBJECT = MAP.parameterizedBy(STRING, NULLABLE_ANY)
private val ENUM_CONSTANT = Regex("[A-Za-z_][A-Za-z0-9_]*")

/** The result of reading a document, including which encoding it turned out to use. */
data class ParsedDocument(val dialect: OpenApiDialect, val resources: List<ResourceDefinition>)

/** A resource object plus the envelope it was found in, which carries `included`. */
private class ResourceCandidate(
    val componentName: String,
    val envelope: FlatSchema,
    val resourceObject: FlatSchema,
)

/**
 * Reads a JSON:API-flavoured OpenAPI document and recovers its resource objects.
 *
 * The document is untrusted input: anything that cannot be mapped unambiguously raises
 * [OpenApiSchemaException] instead of being guessed.
 */
object OpenApiResourceParser {
    /**
     * @param location A local file path or an HTTP(S) URL pointing at an OpenAPI 3.0/3.1 document.
     */
    fun parse(location: String): ParsedDocument {
        // References are deliberately left unresolved: component names are the only place some
        // generators record the resource type.
        val options = ParseOptions().apply { isResolve = false }
        val result = runCatching { OpenAPIV3Parser().readLocation(location, null, options) }
            .getOrElse { throw OpenApiSchemaException("Cannot read the OpenAPI document at '$location'.", it) }
        val openApi = result?.openAPI
            ?: throw OpenApiSchemaException(
                "Cannot parse the OpenAPI document at '$location': ${result?.messages?.joinToString() ?: "no output"}"
            )
        return parse(openApi)
    }

    /** Recovers the resource objects declared under `components.schemas`. */
    fun parse(openApi: OpenAPI): ParsedDocument {
        val components = openApi.components?.schemas
            ?: throw OpenApiSchemaException("The OpenAPI document declares no components.schemas.")
        val resolver = SchemaResolver(components)

        val candidates = components.mapNotNull { (name, schema) -> candidateOf(resolver, name, schema) }
        val dialect = if (candidates.any { pinnedTypeOf(resolver, it.resourceObject) != null }) {
            OpenApiDialect.PINNED_TYPE
        } else {
            OpenApiDialect.COMPONENT_NAME
        }

        // Two passes: relationship targets can only be matched once every resource type is known.
        val typed = candidates.map { resourceTypeOf(resolver, it, dialect) to it }
        val knownTypes = typed.map { it.first }.toSet()

        val resources = typed
            .map { (resourceType, candidate) ->
                definitionOf(openApi, resolver, resourceType, candidate, dialect, knownTypes)
            }
            // A document usually describes the same resource several times (request body, single
            // response, collection response). Keep the richest description of each type.
            .groupBy { it.resourceType }
            .map { (_, duplicates) -> duplicates.maxBy { it.attributes.size + it.relations.size } }
            .sortedBy { it.resourceType }

        return ParsedDocument(dialect, resources)
    }

    /**
     * A component is a resource object when it carries `attributes` or `relationships`, with or
     * without the surrounding `data` envelope.
     */
    private fun candidateOf(resolver: SchemaResolver, componentName: String, schema: Schema<*>): ResourceCandidate? {
        val envelope = resolver.flatten(schema)
        val data = envelope.properties["data"]
        val resourceObject = when {
            data == null -> envelope
            else -> {
                val flattened = resolver.flatten(data)
                if (flattened.schema.baseTypeName() == "array") {
                    resolver.flatten(flattened.schema.items ?: return null)
                } else {
                    flattened
                }
            }
        }
        val hasMembers = "attributes" in resourceObject.properties || "relationships" in resourceObject.properties
        return if (hasMembers) ResourceCandidate(componentName, envelope, resourceObject) else null
    }

    /** The single `type` value a resource object or identifier pins itself to, if any. */
    private fun pinnedTypeOf(resolver: SchemaResolver, resourceObject: FlatSchema): String? {
        val type = resourceObject.properties["type"]?.let(resolver::deref) ?: return null
        type.enum?.filterNotNull()?.singleOrNull()?.let { return it.toString() }
        return type.const?.toString()
    }

    private fun resourceTypeOf(
        resolver: SchemaResolver,
        candidate: ResourceCandidate,
        dialect: OpenApiDialect,
    ): String = when (dialect) {
        OpenApiDialect.PINNED_TYPE -> pinnedTypeOf(resolver, candidate.resourceObject)
            ?: throw OpenApiSchemaException(
                "Component '${candidate.componentName}' looks like a JSON:API resource object but its `type` " +
                    "member declares no single enum/const value, while the rest of the document does pin its types."
            )
        // API Platform names the envelope after the resource and suffixes the representation,
        // and serializes `type` as that same short name verbatim.
        OpenApiDialect.COMPONENT_NAME -> candidate.componentName.substringBefore('.')
    }

    private fun definitionOf(
        openApi: OpenAPI,
        resolver: SchemaResolver,
        resourceType: String,
        candidate: ResourceCandidate,
        dialect: OpenApiDialect,
        knownTypes: Set<String>,
    ): ResourceDefinition {
        val className = resourceType.toPascalCase()
        val attributes = candidate.resourceObject.properties["attributes"]
            ?.let(resolver::flatten)
            ?.let { attributes ->
                attributes.properties.map { (jsonName, schema) ->
                    attributeOf(resolver, className, candidate.componentName, jsonName, schema, jsonName in attributes.required)
                }
            }
            .orEmpty()

        val includedTypes = resolver
            .refNamesOf(candidate.envelope.properties["included"]?.let(resolver::flatten)?.schema?.items)
            .map { it.substringBefore('.') }
            .filter { it in knownTypes }
            .distinct()

        val relations = candidate.resourceObject.properties["relationships"]
            ?.let(resolver::flatten)
            ?.let { relationships ->
                relationships.properties.map { (jsonName, schema) ->
                    relationOf(
                        resolver, candidate.componentName, resourceType, jsonName, schema,
                        required = jsonName in relationships.required,
                        dialect = dialect, knownTypes = knownTypes, includedTypes = includedTypes,
                    )
                }
            }
            .orEmpty()

        return ResourceDefinition(
            resourceType = resourceType,
            endpoint = endpointOf(openApi, resolver, resourceType, dialect),
            className = className,
            attributes = attributes,
            relations = relations,
        )
    }

    private fun attributeOf(
        resolver: SchemaResolver,
        className: String,
        componentName: String,
        jsonName: String,
        raw: Schema<*>,
        required: Boolean,
    ): AttributeDefinition {
        val schema = resolver.deref(raw)
        return AttributeDefinition(
            jsonName = jsonName,
            propertyName = jsonName.toCamelCase(),
            type = kotlinTypeOf(resolver, schema, componentName, jsonName),
            nullable = !required || schema.isNullable(),
            // `readOnly` members are server-generated, so the resource exposes them without a setter.
            mutable = schema.readOnly != true,
            enum = enumOf(className, jsonName, schema),
        )
    }

    /**
     * Emits an enum only when every value is already a valid Kotlin identifier: the processor casts
     * the stored attribute through `valueOf(...)`, so constant names must equal the wire values.
     */
    private fun enumOf(className: String, jsonName: String, schema: Schema<*>): EnumDefinition? {
        if (schema.baseTypeName() != "string") return null
        val values = schema.enum?.filterNotNull()?.map(Any::toString)?.takeIf { it.isNotEmpty() } ?: return null
        if (values.any { !ENUM_CONSTANT.matches(it) }) return null
        return EnumDefinition("$className${jsonName.toPascalCase()}", values)
    }

    /**
     * Types must match what the deserializer puts in `Resource.attributes`, not what the document
     * advertises: every JSON number is narrowed to the first of Int/Long/Float/Double that fits its
     * value, and objects are extracted to plain maps.
     */
    private fun kotlinTypeOf(
        resolver: SchemaResolver,
        schema: Schema<*>,
        componentName: String,
        jsonName: String,
    ): TypeName = when (schema.baseTypeName()) {
        "string" -> STRING
        "boolean" -> BOOLEAN
        "integer" -> INT
        "number" -> FLOAT
        // Item schemas are routinely omitted or left empty for untyped collections.
        "array" -> LIST.parameterizedBy(
            schema.items
                ?.let { kotlinTypeOf(resolver, resolver.deref(it), componentName, jsonName) }
                ?: NULLABLE_ANY
        )
        "object" -> DYNAMIC_OBJECT
        else -> NULLABLE_ANY
    }

    private fun relationOf(
        resolver: SchemaResolver,
        componentName: String,
        resourceType: String,
        jsonName: String,
        raw: Schema<*>,
        required: Boolean,
        dialect: OpenApiDialect,
        knownTypes: Set<String>,
        includedTypes: List<String>,
    ): RelationDefinition {
        val describe = { "Relationship '$jsonName' of component '$componentName'" }
        val linkage = resolver.flatten(raw).properties["data"]
            ?: throw OpenApiSchemaException("${describe()} declares no `data` member, so its cardinality is unknown.")
        val branch = resolver.branch(linkage, describe)

        // Cardinality comes from the linkage shape only, never from the relationship name.
        val toMany = branch.schema.baseTypeName() == "array"
        val identifier = if (toMany) {
            branch.schema.items ?: throw OpenApiSchemaException("${describe()} is to-many without an `items` schema.")
        } else {
            linkageBranchSchema(resolver, branch, describe)
        }

        return RelationDefinition(
            jsonName = jsonName,
            propertyName = jsonName.toCamelCase(),
            targetResourceType = targetTypeOf(
                resolver, identifier, jsonName, describe, dialect, knownTypes, includedTypes
            ),
            toMany = toMany,
            nullable = !toMany && (branch.nullable || !required),
        )
    }

    private fun linkageBranchSchema(resolver: SchemaResolver, branch: Branch, describe: () -> String): Schema<*> =
        branch.schema.takeIf { it.typeNames().isNotEmpty() || it.properties != null || resolver.refName(it) != null }
            ?: throw OpenApiSchemaException("${describe()} declares an empty resource identifier schema.")

    /**
     * Resolution order: the identifier's own pinned `type`, then the component it references, then a
     * single candidate from the envelope's `included`, then a name match against a known type.
     */
    private fun targetTypeOf(
        resolver: SchemaResolver,
        identifier: Schema<*>,
        jsonName: String,
        describe: () -> String,
        dialect: OpenApiDialect,
        knownTypes: Set<String>,
        includedTypes: List<String>,
    ): String {
        pinnedTypeOf(resolver, resolver.flatten(identifier))?.let { return it }
        resolver.refName(identifier)?.substringBefore('.')?.takeIf { it in knownTypes }?.let { return it }
        if (dialect == OpenApiDialect.COMPONENT_NAME) {
            includedTypes.singleOrNull()?.let { return it }
            val byName = jsonName.toPascalCase()
            knownTypes.firstOrNull { it.equals(byName, ignoreCase = true) }?.let { return it }
        }
        throw OpenApiSchemaException(
            "${describe()} does not identify the resource it links to. Candidates from `included`: " +
                "${includedTypes.ifEmpty { listOf("none") }}. Pin the identifier's `type` or name the " +
                "relationship after its resource."
        )
    }

    /**
     * Finds the shortest parameterless path that serves this resource, first by inspecting the `GET`
     * response payload and then by operation tag. Falls back to the resource type itself, which
     * keeps the emitted `endpoint` explicit either way.
     */
    private fun endpointOf(
        openApi: OpenAPI,
        resolver: SchemaResolver,
        resourceType: String,
        dialect: OpenApiDialect,
    ): String {
        val collections = openApi.paths.orEmpty()
            .filterKeys { !it.contains('{') }
            .entries
            .sortedWith(compareBy({ it.key.length }, { it.key }))

        val byPayload = collections.firstOrNull { (_, pathItem) ->
            pathItem.get?.responses?.values.orEmpty().any { response ->
                response.content?.values.orEmpty().any { media ->
                    val payload = media.schema?.let(resolver::flatten)?.properties?.get("data") ?: return@any false
                    val items = resolver.flatten(payload).schema.items ?: return@any false
                    val candidate = candidateOf(resolver, resolver.refName(items).orEmpty(), items)
                    candidate != null && runCatching { resourceTypeOf(resolver, candidate, dialect) }
                        .getOrNull() == resourceType
                }
            }
        }
        // API Platform leaves collection payloads inline but tags every operation with the short name.
        val byTag = collections.firstOrNull { (_, pathItem) -> pathItem.get?.tags?.contains(resourceType) == true }

        return (byPayload ?: byTag)?.key?.trimStart('/') ?: resourceType
    }
}
