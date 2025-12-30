package it.maicol07.spraypaintkt_processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getKotlinClassByName
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.originatingKSFiles
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toKModifier
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import it.maicol07.spraypaintkt.Resource
import it.maicol07.spraypaintkt.ResourceRegistry
import it.maicol07.spraypaintkt.ResourceSerializer
import it.maicol07.spraypaintkt.Scope
import it.maicol07.spraypaintkt.interfaces.JsonApiConfig
import it.maicol07.spraypaintkt.util.pluralize
import it.maicol07.spraypaintkt_annotation.Attr
import it.maicol07.spraypaintkt_annotation.DefaultInstance
import it.maicol07.spraypaintkt_annotation.Relation
import it.maicol07.spraypaintkt_annotation.ResourceSchema
import kotlinx.serialization.Serializable
import net.pearx.kasechange.toSnakeCase
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/** Processes [ResourceSchema] annotations in order to create 'model' representations of annotated classes. */
class ResourceSchemaProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {
    lateinit var resolver: Resolver
    private var scopeClass: KSClassDeclaration? = null

    override fun process(resolver: Resolver): List<KSAnnotated> {
        this.resolver = resolver
        this.scopeClass = resolver.getClassDeclarationByName(Scope::class.qualifiedName!!)

        // Create function to initialize ResourceFactory
        val registerResourcesFun = FunSpec.builder("registerResources")
            .receiver(ResourceRegistry::class)

        val symbols = resolver.getSymbolsWithAnnotation(ResourceSchema::class.qualifiedName!!)
            .filterIsInstance(KSClassDeclaration::class.java)
            .toList()
        if (symbols.isEmpty()) return emptyList()

        val defaultConfig = getDefaultConfig(resolver)

        for (resourceSchema in symbols) {
            logger.info("Found annotated class: ${resourceSchema.qualifiedName?.asString()}")

            if (!resourceSchema.simpleName.asString().endsWith("Schema")) {
                logger.error("ResourceSchema class does not end with 'Schema'", resourceSchema)
                continue
            }

            if (!resourceSchema.isAbstract()) {
                logger.error(
                    "ResourceSchema class must be an interface or abstract class",
                    resourceSchema
                )
                continue
            }

            val resourceSimpleName = resourceSchema.simpleName.asString().removeSuffix("Schema")
            val resourceClassName = ClassName.bestGuess(
                resourceSchema.qualifiedName!!.asString().removeSuffix("Schema")
            )
            val resourceClass = generateResourceClass(
                resolver,
                resourceSchema,
                resourceSimpleName,
                resourceClassName,
                defaultConfig
            )

            val resourceFile =
                FileSpec.builder(resourceSchema.packageName.asString(), resourceClass.name!!)
                    .addType(resourceClass)
                    .build()
            logger.info("Writing file ${resourceFile.name}")
            resourceFile.writeTo(
                codeGenerator,
                false,
                listOfNotNull(defaultConfig?.containingFile, *resourceFile.originatingKSFiles().toTypedArray())
            )

            registerResourcesFun.addStatement(
                "registerResource<%T>(%T.Companion)",
                resourceClassName,
                resourceClassName
            )
        }

        val file = FileSpec.builder("it.maicol07.spraypaintkt", "ResourceFactoryInitializer")
            .addFunction(registerResourcesFun.build())
            .build()
        file.writeTo(codeGenerator, false, symbols.toList().mapNotNull { it.containingFile })

        val unableToProcess = symbols.filterNot { it.validate() }.toList()
        return unableToProcess
    }

    @OptIn(KspExperimental::class)
    fun generateResourceClass(
        resolver: Resolver,
        resourceSchema: KSClassDeclaration,
        resourceSimpleName: String,
        resourceClassName: ClassName,
        defaultConfig: KSClassDeclaration?
    ): TypeSpec {
        // Workaround for https://github.com/google/ksp/issues/2356
        val resourceSchemaAnnotation = ResourceSchema(
            resourceSchema.getAnnotationValue<ResourceSchema, String>("resourceType")!!,
            resourceSchema.getAnnotationValue<ResourceSchema, String>("endpoint")!!,
            resourceSchema.getAnnotationValue<ResourceSchema, KClass<out JsonApiConfig>>("config")!!
        )

        val attributes = getAttributesInfo(resourceSchema)
        val relationships = getRelationshipsInfo(resolver, resourceSchema)

        logger.info("Generating resource class: $resourceSimpleName")

        return TypeSpec.classBuilder(resourceSimpleName)
            .addOriginatingKSFile(resourceSchema.containingFile!!)
            .addSuperinterface(Resource::class)
            .let {
                when (resourceSchema.classKind) {
                    ClassKind.INTERFACE -> it.addSuperinterface(resourceSchema.toClassName())
                    ClassKind.CLASS -> it.superclass(resourceSchema.toClassName())
                    else -> throw IllegalArgumentException("ResourceSchema must be an interface or an abstract class") // Should never happen, since we already checked this before
                }
            }
            .addType(
                generateResourceCompanionObject(
                    resourceSchema,
                    resourceClassName,
                    resourceSchemaAnnotation,
                    defaultConfig
                )
            )
            .addProperty(
                PropertySpec.builder(
                    "companion",
                    Resource.CompanionObj::class.asTypeName().parameterizedBy(resourceClassName)
                )
                    .addModifiers(KModifier.OVERRIDE)
                    .initializer("Companion")
                    .build()
            )
            .addProperties(generateBaseResourceProperties())
            .addAnnotation(
                AnnotationSpec.builder(Serializable::class.asClassName())
                    .addMember("%T.Companion.Serializer::class", resourceClassName).build()
            )
            .addProperties(attributes.map { it.spec })
            .addProperties(relationships.map { it.spec })
            .addFunction(generateConstructor(attributes + relationships))
            .build()
    }

    private fun generateResourceCompanionObject(
        resourceSchema: KSClassDeclaration,
        resourceClassName: ClassName,
        resourceSchemaAnnotation: ResourceSchema,
        defaultConfig: KSClassDeclaration?
    ) = TypeSpec.companionObjectBuilder()
        .addSuperinterface(
            Resource.CompanionObj::class.asTypeName()
                .parameterizedBy(resourceClassName)
        )
        .addType(
            TypeSpec.objectBuilder("Serializer")
                .superclass(
                    ResourceSerializer::class.asTypeName()
                        .parameterizedBy(resourceClassName)
                )
                .build()
        )
        .addProperty(
            PropertySpec.builder("resourceType", String::class)
                .addModifiers(KModifier.OVERRIDE)
                .initializer("%S", resourceSchemaAnnotation.resourceType.ifEmpty {
                    resourceClassName.simpleName.lowercase()
                })
                .build()
        )
        .addProperty(
            PropertySpec.builder("endpoint", String::class)
                .addModifiers(KModifier.OVERRIDE)
                .initializer("%S", resourceSchemaAnnotation.endpoint.ifEmpty {
                    resourceClassName.simpleName.lowercase().pluralize()
                })
                .build()
        )
        .addProperty(
            PropertySpec.builder("config", JsonApiConfig::class)
                .addModifiers(KModifier.OVERRIDE)
                .initializer(
                    "%T", (try {
                        resourceSchemaAnnotation.config
                    } catch (_: NoSuchElementException) {
                        JsonApiConfig::class
                    }).let {
                        if (it.qualifiedName == JsonApiConfig::class.qualifiedName || !it.isSubclassOf(
                                JsonApiConfig::class
                            )
                        )
                            defaultConfig?.asType(emptyList())?.toTypeName()
                                ?: logger.error("No default JsonApiConfig found. Please provide a config in the ResourceSchema annotation or annotate a JsonApiConfig object with @DefaultInstance")
                        else it
                    })
                .build()
        )
        .addProperty(
            PropertySpec.builder("factory", LambdaTypeName.get(returnType = resourceClassName))
                .addModifiers(KModifier.OVERRIDE)
                .initializer("{ %T() }", resourceClassName)
                .build()
        )
        .addProperty(
            PropertySpec.builder(
                "schema",
                KClass::class.asTypeName().parameterizedBy(resourceSchema.toClassName())
            )
                .addModifiers(KModifier.OVERRIDE)
                .initializer("%T::class", resourceSchema.toClassName())
                .build()
        )
        .addFunctions(generateScopeFunctions(resourceClassName))
        .addInitializerBlock(
            CodeBlock.builder()
                .addStatement(
                    "%T.%M()",
                    ResourceRegistry::class,
                    MemberName("it.maicol07.spraypaintkt", "registerResources")
                )
                .build()
        )
        .build()

    private val baseResourceProperties = mapOf(
        "id" to CodeBlock.of("%L", "null"),
        "isPersisted" to CodeBlock.of("%L", false),
        "attributes" to CodeBlock.of(
            "%L.%M()",
            "mutableMapOf<String, Any?>()",
            MemberName("it.maicol07.spraypaintkt.extensions.", "trackChanges")
        ),
        "relationships" to CodeBlock.of(
            "%L.%M()",
            "mutableMapOf<String, Any?>()",
            MemberName("it.maicol07.spraypaintkt.extensions.", "trackChanges")
        ),
        "meta" to CodeBlock.of("%L", "mutableMapOf()"),
        "links" to CodeBlock.of("%L", "mutableMapOf()"),
        "type" to CodeBlock.of("%L", "lazy { companion.resourceType }")
    )
    private val baseResourcePropertiesWithDelegate = listOf("type")

    private fun generateBaseResourceProperties(): Iterable<PropertySpec> =
        resolver.getClassDeclarationByName(Resource::class.qualifiedName!!)!!
            .getAllProperties()
            .filter { it.simpleName.asString() in baseResourceProperties.keys }
            .map { property ->
                val propertyName = property.simpleName.asString()
                PropertySpec.builder(propertyName, property.type.toTypeName())
                    .addModifiers(KModifier.OVERRIDE)
                    .mutable(property.isMutable)
                    .let {
                        if (propertyName in baseResourcePropertiesWithDelegate) {
                            it.delegate(baseResourceProperties[propertyName]!!)
                        } else {
                            it.initializer(baseResourceProperties[propertyName]!!)
                        }
                    }
                    .build()
            }
            .asIterable()

    @OptIn(KspExperimental::class)
    private fun getAttributesInfo(resourceSchema: KSClassDeclaration): List<PropertyInfo> =
        resourceSchema.getAllProperties()
            .filter { it.isAnnotationPresent(Attr::class) }
            .map { property ->
                // Workaround for https://github.com/google/ksp/issues/2356
                val annotation = Attr(
                    name = property.getAnnotationValue<Attr, String>("name")!!,
                    autoTransform = property.getAnnotationValue<Attr, Boolean>("autoTransform")!!,
                    mutable = property.getAnnotationValue<Attr, Boolean>("mutable")!!
                )
                val propertyName = property.simpleName.asString()
                val attributeName =
                    annotation.name.ifEmpty { if (annotation.autoTransform) propertyName.toSnakeCase() else propertyName }

                logger.info("Generating attribute $propertyName of type ${property.type}")

                // Check if property type is enum
                val propertyType = property.type.resolve()
                val classDeclaration = resolver.getClassDeclarationByName(propertyType.declaration.qualifiedName!!)
                val isEnum = classDeclaration?.classKind == ClassKind.ENUM_CLASS

                val spec = PropertySpec.builder(propertyName, property.type.toTypeName())
                    .addModifiers(KModifier.OVERRIDE)
                    .mutable(property.isMutable || annotation.mutable)
                    .getter(
                        FunSpec.getterBuilder()
                            .addCode("return ")
                            .beginControlFlow("if (attributes.containsKey(%S))", attributeName)
                            .addStatement(
                                if (isEnum) "%T.valueOf(attributes[%S] as String)" else "attributes[%S] as %T",
                                if (isEnum) property.type.toTypeName().copy(false) else attributeName,
                                if (isEnum) attributeName else property.type.toTypeName()
                            )
                            .nextControlFlow("else")
                            .addStatement(
                                if (!property.isAbstract()) "super.$propertyName"
                                else (if (property.type.resolve().isMarkedNullable) "null" else "throw NoSuchElementException(\"$propertyName not found\")")
                            )
                            .endControlFlow()
                            .build()
                    )
                    .let {
                        if (property.isMutable || annotation.mutable) {
                            it.setter(
                                FunSpec.setterBuilder()
                                    .addParameter("value", property.type.toTypeName())
                                    .addStatement("attributes[%S] = value", attributeName)
                                    .build()
                            )
                        } else it
                    }
                    .build()
                PropertyInfo(spec, attributeName, true)
            }.toList()

    @OptIn(KspExperimental::class)
    private fun getRelationshipsInfo(
        resolver: Resolver,
        resourceSchema: KSClassDeclaration
    ): List<PropertyInfo> = resourceSchema.getAllProperties()
        .filter { it.isAnnotationPresent(Relation::class) }
        .map { property ->
            // Workaround for https://github.com/google/ksp/issues/2356
            val annotation = Relation(
                name = property.getAnnotationValue<Attr, String>("name")!!,
                autoTransform = property.getAnnotationValue<Attr, Boolean>("autoTransform")!!,
                mutable = property.getAnnotationValue<Attr, Boolean>("mutable")!!
            )
            val propertyName = property.simpleName.asString()
            val relationName =
                annotation.name.ifEmpty { if (annotation.autoTransform) propertyName.toSnakeCase() else propertyName }
            logger.info("Generating attribute $propertyName of type ${property.type}")

            val relationType = property.type.resolve()
            val isToMany =
                relationType.declaration.qualifiedName?.asString() in listOf(
                    "kotlin.collections.List",
                    "kotlin.collections.MutableList"
                ) && relationType.arguments.isNotEmpty()
            val relationResourceSchema = if (isToMany) {
                val typeArgument = relationType.arguments.first()
                typeArgument.type!!.resolve()
            } else relationType

            if (property.isMutable) {
                logger.error("Relationship is a var property where a val is expected", property)
                return@map null
            }

            logger.info("Generating relationship $propertyName of type $resourceSchema")

            // Check if the type is a ResourceSchema
            val relationResourceSchemaName = relationResourceSchema.declaration.qualifiedName?.asString()
            if (relationResourceSchemaName == null) {
                logger.error("Relation ResourceSchema not found", property)
                return@map null
            }
            val relationResourceSchemaResolved = resolver.getKotlinClassByName(relationResourceSchemaName)
            if (
                relationResourceSchemaResolved!!.annotations.none {
                    it.annotationType.resolve().declaration.qualifiedName?.asString() == ResourceSchema::class.qualifiedName
                }
            ) {
                logger.error("Relationship type is not a ResourceSchema", property)
                return@map null
            }

            val resourceTypeName = ClassName.bestGuess(
                relationResourceSchema.declaration.qualifiedName!!.asString().removeSuffix("Schema")
            )

            val realType = if (isToMany) {
                ClassName(
                    "kotlin.collections",
                    if (annotation.mutable) "MutableList" else "List"
                ).parameterizedBy(resourceTypeName).copy(nullable = relationType.isMarkedNullable)
            } else {
                resourceTypeName.copy(nullable = relationType.isMarkedNullable)
            }

            val spec = PropertySpec.builder(propertyName, realType)
                .mutable(annotation.mutable)
                .getter(
                    FunSpec.getterBuilder()
                        .let { builder ->
                            if (isToMany) {
                                builder.addAnnotation(
                                    AnnotationSpec.builder(Suppress::class)
                                        .addMember("%S", "UNCHECKED_CAST").build()
                                )
                            } else builder
                        }
                        .addCode("return ")
                        .beginControlFlow(
                            "if (relationships.containsKey(%S) && (${
                                if (realType.isNullable) "relationships[%S] == null || "
                                else ""
                            }relationships[%S] is %T))",
                            *listOfNotNull(
                                relationName,
                                if (realType.isNullable) relationName else null,
                                relationName,
                                if (realType is ParameterizedTypeName) realType.copy(
                                    typeArguments = listOf(
                                        STAR
                                    )
                                ) else realType
                            ).toTypedArray(),
                        )
                        .addStatement("relationships[%S] as %T", relationName, realType)
                        .nextControlFlow("else")
                        .addStatement(
                            if (!property.isAbstract()) {
                                if (isToMany) "(super.$propertyName as %T).%M()"
                                else "super.$propertyName"
                            } else (if (isToMany) {
                                if (realType.isNullable) "null" else "mutableListOf<%T>().%M()"
                            } else {
                                if (realType.isNullable) "null" else ("throw NoSuchElementException(\"$propertyName not found\")")
                            }),
                            if (!property.isAbstract() && isToMany) realType else resourceTypeName,
                            MemberName("it.maicol07.spraypaintkt.extensions.", "trackChanges")
                        )
                        .endControlFlow()
                        .build()
                )
                .let {
                    if (annotation.mutable) {
                        it.setter(
                            FunSpec.setterBuilder()
                                .addParameter("value", realType)
                                .addStatement("relationships[%S] = value", relationName)
                                .build()
                        )
                    } else it
                }
                .addModifiers(KModifier.OVERRIDE)
                .build()
            PropertyInfo(spec, relationName, false)
        }
        .filterNotNull()
        .toList()

    private fun getDefaultConfig(resolver: Resolver): KSClassDeclaration? =
        resolver.getSymbolsWithAnnotation(DefaultInstance::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .filter { declaration -> declaration.superTypes.any { it.resolve().declaration.qualifiedName?.asString() == JsonApiConfig::class.qualifiedName } }
            .firstOrNull()

    @OptIn(KspExperimental::class)
    private fun generateScopeFunctions(resourceType: TypeName): List<FunSpec> {
        val scope = scopeClass ?: return emptyList()
        val methods = scope.getDeclaredFunctions()
        logger.info("Generating scope functions for $resourceType: ${methods.joinToString { it.simpleName.asString() }}")
        val parameterizedScopeType = Scope::class.asTypeName().parameterizedBy(resourceType)
        return listOf(
            FunSpec.builder("scope")
                .addParameter(
                    ParameterSpec.builder(
                        "options", LambdaTypeName.get(
                            receiver = parameterizedScopeType,
                            returnType = Unit::class.java.asTypeName()
                        )
                    )
                        .defaultValue("{}")
                        .build()
                )
                .returns(parameterizedScopeType)
                .addStatement("return %T(%T::class, options)", Scope::class, resourceType)
                .build()
        ) + methods
            .filter { it.isAnnotationPresent(Scope.ScopeMethod::class) }
            .onEach { logger.info("Generating method ${it.simpleName.asString()}") }
            .map { method ->
                logger.info("Generating method ${method.simpleName.asString()}")
                val params = method.parameters.filter { it.name != null }
                val returnType = method.returnType!!.resolve().let {
                    val typeArgument = it.arguments.firstOrNull()?.type?.toString()
                    if (typeArgument == "R" || typeArgument == "R?") {
                        (it.declaration as KSClassDeclaration)
                            .toClassName()
                            .parameterizedBy(resourceType.copy(nullable = typeArgument == "R?"))
                    } else {
                        it.toTypeName()
                    }
                }

                FunSpec.builder(method.simpleName.asString())
                    .addModifiers(method.modifiers.mapNotNull { it.toKModifier() })
                    .addParameters(params.map {
                        ParameterSpec.builder(
                            it.name!!.asString(),
                            it.type.toTypeName()
                        )
                            .addModifiers(mutableListOf<KModifier>().apply {
                                if (it.isVararg) add(KModifier.VARARG)
                            })
                            .build()
                    })
                    .returns(returnType)
                    .addStatement(
                        "return scope().%L(%L)",
                        method.simpleName.asString(),
                        params.joinToString(", ") { (if (it.isVararg) "*" else "") + it.name!!.asString() })
                    .build()
            }
    }

    private fun generateConstructor(properties: List<PropertyInfo>): FunSpec {
        val builder = FunSpec.constructorBuilder()

        for (property in properties) {
            val paramName = property.spec.name
            val paramType = property.spec.type.copy(nullable = true)

            builder.addParameter(
                ParameterSpec.builder(paramName, paramType)
                    .defaultValue("null")
                    .build()
            )

            val mapName = if (property.isAttribute) "attributes" else "relationships"

            builder.addCode(
                CodeBlock.builder()
                    .beginControlFlow("if (%N != null)", paramName)
                    .addStatement("%N[%S] = %N", mapName, property.jsonName, paramName)
                    .endControlFlow()
                    .build()
            )
        }

        return builder.build()
    }

    private data class PropertyInfo(
        val spec: PropertySpec,
        val jsonName: String,
        val isAttribute: Boolean
    )
}
