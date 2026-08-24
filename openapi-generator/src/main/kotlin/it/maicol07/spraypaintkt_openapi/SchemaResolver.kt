package it.maicol07.spraypaintkt_openapi

import io.swagger.v3.oas.models.media.Schema

/** A schema with its `allOf` chain merged into a single property map. */
internal class FlatSchema(
    val schema: Schema<*>,
    val properties: Map<String, Schema<*>>,
    val required: Set<String>,
)

/** The single meaningful alternative of a `oneOf`/`anyOf`, plus whether `null` was one of them. */
internal class Branch(val schema: Schema<*>, val nullable: Boolean)

/**
 * Navigates a document whose `$ref`s are left intact.
 *
 * The generator needs component names to recover resource types from documents that do not pin
 * `type` to a literal, so references cannot be inlined by the parser.
 */
internal class SchemaResolver(private val components: Map<String, Schema<*>>) {
    /** The component a schema points at, or `null` when the schema is inline. */
    fun refName(schema: Schema<*>): String? = schema.`$ref`?.substringAfterLast('/')

    fun deref(schema: Schema<*>): Schema<*> {
        var current = schema
        val seen = mutableSetOf<String>()
        while (true) {
            val name = refName(current) ?: return current
            if (!seen.add(name)) throw OpenApiSchemaException("Circular \$ref chain through component '$name'.")
            current = components[name] ?: throw OpenApiSchemaException("Unresolvable \$ref target '$name'.")
        }
    }

    fun flatten(schema: Schema<*>): FlatSchema {
        val resolved = deref(schema)
        val properties = linkedMapOf<String, Schema<*>>()
        val required = mutableSetOf<String>()

        fun visit(current: Schema<*>, seen: MutableSet<Schema<*>>) {
            val target = deref(current)
            if (!seen.add(target)) return
            target.allOf?.forEach { visit(it, seen) }
            target.properties?.forEach { (name, property) -> properties[name] = property }
            target.required?.let(required::addAll)
        }
        visit(resolved, mutableSetOf())

        return FlatSchema(resolved, properties, required)
    }

    /**
     * Unwraps the `oneOf`/`anyOf` wrapper some generators use to express a nullable member.
     * More than one non-null alternative is ambiguous and is reported to the caller.
     */
    fun branch(schema: Schema<*>, describe: () -> String): Branch {
        val resolved = deref(schema)
        val alternatives = resolved.oneOf ?: resolved.anyOf
            ?: return Branch(resolved, nullable = resolved.isNullable())
        val concrete = alternatives.filterNot { deref(it).isNullType() }
        if (concrete.size != 1) {
            throw OpenApiSchemaException(
                "${describe()} offers ${concrete.size} non-null alternatives, so its shape is ambiguous."
            )
        }
        return Branch(deref(concrete.single()), nullable = concrete.size < alternatives.size)
    }

    /** The component names an `anyOf`/`oneOf`/`$ref` list of item schemas points at. */
    fun refNamesOf(schema: Schema<*>?): List<String> {
        if (schema == null) return emptyList()
        refName(schema)?.let { return listOf(it) }
        val alternatives = schema.anyOf ?: schema.oneOf ?: return emptyList()
        return alternatives.mapNotNull(::refName)
    }
}

/** OpenAPI 3.1 replaces `nullable` with `null` inside a type union, so both spellings are read. */
internal fun Schema<*>.typeNames(): Set<String> = types ?: type?.let(::setOf) ?: emptySet()

internal fun Schema<*>.baseTypeName(): String? = typeNames().firstOrNull { it != "null" }

internal fun Schema<*>.isNullType(): Boolean = typeNames() == setOf("null")

internal fun Schema<*>.isNullable(): Boolean = nullable == true || "null" in typeNames()
