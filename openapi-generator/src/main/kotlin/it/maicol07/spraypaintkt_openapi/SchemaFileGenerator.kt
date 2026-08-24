package it.maicol07.spraypaintkt_openapi

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import net.pearx.kasechange.toSnakeCase

private const val ANNOTATION_PACKAGE = "it.maicol07.spraypaintkt_annotation"
private val RESOURCE_SCHEMA = ClassName(ANNOTATION_PACKAGE, "ResourceSchema")
private val ATTR = ClassName(ANNOTATION_PACKAGE, "Attr")
private val RELATION = ClassName(ANNOTATION_PACKAGE, "Relation")

/**
 * Emits one `*Schema.kt` source per [ResourceDefinition], ready for the KSP processor.
 *
 * @param packageName The package the generated schemas belong to.
 */
class SchemaFileGenerator(private val packageName: String) {
    /** @return the generated sources, keyed by file name. */
    fun generate(definitions: List<ResourceDefinition>): Map<String, String> {
        val knownTypes = definitions.associateBy { it.resourceType }
        return definitions.associate { definition ->
            "${definition.className}Schema.kt" to fileSpecOf(definition, knownTypes).toString()
        }
    }

    private fun fileSpecOf(
        definition: ResourceDefinition,
        knownTypes: Map<String, ResourceDefinition>,
    ): FileSpec {
        val schemaName = "${definition.className}Schema"
        val schema = TypeSpec.interfaceBuilder(schemaName)
            .addAnnotation(
                AnnotationSpec.builder(RESOURCE_SCHEMA)
                    // Both values stay explicit: deriving them at build time would tie the wire format
                    // to the class name.
                    .addMember("resourceType = %S", definition.resourceType)
                    .addMember("endpoint = %S", definition.endpoint)
                    .build()
            )
            .addProperties(definition.attributes.map(::attributeProperty))
            .addProperties(definition.relations.map { relationProperty(definition, it, knownTypes) })
            .build()

        return FileSpec.builder(packageName, schemaName)
            .indent("    ")
            .addType(schema)
            .apply {
                definition.attributes.mapNotNull { it.enum }.distinctBy { it.className }.forEach { addType(enumType(it)) }
            }
            .build()
    }

    /** Constants are the wire values verbatim: the processor round-trips them via `valueOf`/`name`. */
    private fun enumType(enum: EnumDefinition): TypeSpec =
        TypeSpec.enumBuilder(enum.className)
            .apply { enum.values.forEach(::addEnumConstant) }
            .build()

    private fun attributeProperty(attribute: AttributeDefinition): PropertySpec {
        val base = attribute.enum?.let { ClassName(packageName, it.className) } ?: attribute.type
        return PropertySpec.builder(attribute.propertyName, base.copy(nullable = attribute.nullable))
            .mutable(attribute.mutable)
            .addAnnotation(attributeAnnotation(attribute))
            .build()
    }

    private fun relationProperty(
        owner: ResourceDefinition,
        relation: RelationDefinition,
        knownTypes: Map<String, ResourceDefinition>,
    ): PropertySpec {
        val target = knownTypes[relation.targetResourceType]
            ?: throw OpenApiSchemaException(
                "Relationship '${relation.jsonName}' of resource '${owner.resourceType}' links to type " +
                    "'${relation.targetResourceType}', which the document does not describe as a resource object."
            )
        val targetName = ClassName(packageName, "${target.className}Schema")
        val type: TypeName =
            if (relation.toMany) LIST.parameterizedBy(targetName) else targetName.copy(nullable = relation.nullable)
        return PropertySpec.builder(relation.propertyName, type)
            .addAnnotation(nameAnnotation(RELATION, relation.jsonName, relation.propertyName))
            .build()
    }

    private fun attributeAnnotation(attribute: AttributeDefinition): AnnotationSpec {
        val builder = AnnotationSpec.builder(ATTR)
        if (attribute.jsonName != attribute.propertyName.toSnakeCase()) {
            builder.addMember("name = %S", attribute.jsonName)
        }
        // `mutable` defaults to true in the annotation, so only read-only members need it spelled out.
        if (!attribute.mutable) builder.addMember("mutable = false")
        return builder.build()
    }

    /**
     * Both annotations fall back to the snake-cased property name, so the wire name is only spelled out
     * when that fallback would not reproduce it.
     */
    private fun nameAnnotation(annotation: ClassName, jsonName: String, propertyName: String): AnnotationSpec {
        val builder = AnnotationSpec.builder(annotation)
        if (jsonName != propertyName.toSnakeCase()) builder.addMember("name = %S", jsonName)
        return builder.build()
    }
}
