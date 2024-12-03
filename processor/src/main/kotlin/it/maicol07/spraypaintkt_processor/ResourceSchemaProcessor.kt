package it.maicol07.spraypaintkt_processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getKotlinClassByName
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
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
import com.squareup.kotlinpoet.ksp.toClassName
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
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties

/** Processes [ResourceSchema] annotations in order to create 'model' representations of annotated classes. */
class ResourceSchemaProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {
    private val filesToWrite = mutableListOf<FileSpec>()
    private val fileDependencies = mutableSetOf<KSFile>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // Create function to initialize ResourceFactory
        val fileBuilder = FileSpec.builder("it.maicol07.spraypaintkt", "ResourceFactoryInitializer")
        val registerResourcesFun = FunSpec.builder("registerResources")
            .receiver(ResourceRegistry::class)

        resolver.getSymbolsWithAnnotation(ResourceSchema::class.qualifiedName!!)
            .filterIsInstance(KSClassDeclaration::class.java)
            .forEach { resourceSchema ->
                logger.info("Found annotated class: ${resourceSchema.qualifiedName?.asString()}")

                if (!resourceSchema.simpleName.asString().endsWith("Schema")) {
                    throw IllegalStateException("Class ${resourceSchema.qualifiedName?.asString()} does not end with 'Schema'")
                }

                val defaultConfig = getDefaultConfig(resolver)
                defaultConfig?.let { fileDependencies.add(it.containingFile!!) }
                fileDependencies.add(resourceSchema.containingFile!!)

                val resourceSimpleName = resourceSchema.simpleName.asString().removeSuffix("Schema")
                val resourceClassName = ClassName.bestGuess(resourceSchema.qualifiedName!!.asString().removeSuffix("Schema"))
                val resourceClass = generateResourceClass(resolver, resourceSchema, resourceSimpleName, resourceClassName, defaultConfig)

                val resourceFile =
                    FileSpec.builder(resourceSchema.packageName.asString(), resourceClass.name!!)
                        .addType(resourceClass)
                        .build()
                filesToWrite.add(resourceFile)


                registerResourcesFun.addStatement(
                    "registerResource<%T>(%T.Companion)",
                    resourceClassName,
                    resourceClassName
                )
            }

        val file = fileBuilder
            .addFunction(registerResourcesFun.build())
            .build()

        filesToWrite.add(file)
        return emptyList()
    }

    @OptIn(KspExperimental::class)
    fun generateResourceClass(
        resolver: Resolver,
        resourceSchema: KSClassDeclaration,
        resourceSimpleName: String,
        resourceClassName: ClassName,
        defaultConfig: KSClassDeclaration?
    ): TypeSpec {
        val resourceSchemaAnnotation = resourceSchema.getAnnotationsByType(ResourceSchema::class).first()

        logger.info("Generating resource class: $resourceSimpleName")

        return TypeSpec.classBuilder(resourceSimpleName)
            .addSuperinterface(Resource::class)
            .let {
                when (resourceSchema.classKind) {
                    ClassKind.INTERFACE -> it.addSuperinterface(resourceSchema.toClassName())
                    ClassKind.CLASS -> it.superclass(resourceSchema.toClassName())
                    else -> throw IllegalStateException("ResourceSchema class must be an interface or a class")
                }
            }
            .addType(generateResourceCompanionObject(resourceSchema, resourceClassName, resourceSchemaAnnotation, defaultConfig))
            .addProperty(
                PropertySpec.builder("companion", Resource.CompanionObj::class.asTypeName().parameterizedBy(resourceClassName))
                    .addModifiers(KModifier.OVERRIDE)
                    .initializer("Companion")
                    .build()
            )
            .addProperties(generateBaseResourceProperties())
            .addAnnotation(AnnotationSpec.builder(Serializable::class.asClassName()).addMember("%T::class", ResourceSerializer::class).build())
            .addProperties(generateAttributes(resourceSchema))
            .addProperties(generateRelationships(resolver, resourceSchema))
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
        .addProperty(
            PropertySpec.builder("resourceType", String::class)
                .addModifiers(KModifier.OVERRIDE)
                .initializer("%S", resourceSchemaAnnotation.resourceType.ifEmpty {
                    resourceClassName.simpleName.lowercase().pluralize()
                })
                .build()
        )
        .addProperty(
            PropertySpec.builder("endpoint", String::class)
                .addModifiers(KModifier.OVERRIDE)
                .initializer("%S", resourceSchemaAnnotation.endpoint)
                .build()
        )
        .addProperty(
            PropertySpec.builder("config", JsonApiConfig::class)
                .addModifiers(KModifier.OVERRIDE)
                .initializer(
                    "%T", (try {
                        resourceSchemaAnnotation.config
                    } catch (e: NoSuchElementException) {
                        JsonApiConfig::class
                    }).let {
                        if (it.qualifiedName == JsonApiConfig::class.qualifiedName || !it.isSubclassOf(JsonApiConfig::class))
                            defaultConfig?.asType(emptyList())?.toTypeName()
                                ?: throw IllegalStateException("No default JsonApiConfig found. Please provide a config in the ResourceSchema annotation or annotate a JsonApiConfig object with @DefaultInstance")
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
            PropertySpec.builder("schema", KClass::class.asTypeName().parameterizedBy(resourceSchema.toClassName()))
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

    private val BaseResourceProperties = mapOf(
        "id" to CodeBlock.of("%L", "null"),
        "isPersisted" to CodeBlock.of("%L", false),
        "attributes" to CodeBlock.of("%L.%M()", "mutableMapOf<String, Any?>()", MemberName("it.maicol07.spraypaintkt.extensions.", "trackChanges")),
        "relationships" to CodeBlock.of("%L.%M()", "mutableMapOf<String, Any?>()", MemberName("it.maicol07.spraypaintkt.extensions.", "trackChanges")),
        "meta" to CodeBlock.of("%L", "mutableMapOf()"),
        "links" to CodeBlock.of("%L", "mutableMapOf()"),
        "type" to CodeBlock.of("%L", "lazy { companion.resourceType }")
    )
    private val BaseResourcePropertiesWithDelegate = listOf("type")

    private fun generateBaseResourceProperties(): Iterable<PropertySpec> = Resource::class.memberProperties
        .filter { it.name in BaseResourceProperties.keys }
        .map { property ->
            var returnType = property.returnType.asTypeName()
            // Workaround for https://github.com/square/kotlinpoet/issues/279
            if (property.returnType.toString().startsWith("kotlin.collections.MutableMap")) {
                returnType = ClassName("kotlin.collections", "MutableMap").parameterizedBy(property.returnType.arguments.map { it.type!!.asTypeName() })
            }
            PropertySpec.builder(property.name, returnType)
                .addModifiers(KModifier.OVERRIDE)
                .mutable(property is KMutableProperty<*>)
                .let {
                    if (property.name in BaseResourcePropertiesWithDelegate) {
                        it.delegate(BaseResourceProperties[property.name]!!)
                    } else {
                        it.initializer(BaseResourceProperties[property.name]!!)
                    }
                }
                .build()
        }.asIterable()

    @OptIn(KspExperimental::class)
    fun generateAttributes(resourceSchema: KSClassDeclaration): Iterable<PropertySpec> = resourceSchema.getAllProperties()
        .filter { it.isAnnotationPresent(Attr::class) }
        .map {
            val annotation = it.getAnnotationsByType(Attr::class).first()
            val propertyName = it.simpleName.asString()
            val attributeName = annotation.name.ifEmpty { if (annotation.autoTransform) propertyName.toSnakeCase() else propertyName }
            logger.info("Generating attribute $propertyName of type ${it.type}")

            val delegate = if (it.type.toTypeName().isNullable) "nullableAttribute" else "attribute"

            val delegateFormat = if (!it.isAbstract()) "%M(%S, %L)" else "%M(%S)"
            val delegateParams = listOf(
                MemberName("it.maicol07.spraypaintkt.extensions", delegate),
                attributeName,
                if (!it.isAbstract()) "super.$propertyName" else null
            )

            PropertySpec.builder(propertyName, it.type.toTypeName())
                .addModifiers(KModifier.OVERRIDE)
                .mutable()
                .delegate(delegateFormat, *delegateParams.filterNotNull().toTypedArray())
                .build()
        }.asIterable()

    private data class RelationshipPropertyData(
        var delegateFormat: String,
        var delegateParams: List<Any?>,
        var propertyType: TypeName
    )

    @OptIn(KspExperimental::class)
    private fun generateRelationships(
        resolver: Resolver,
        resourceSchema: KSClassDeclaration
    ): Iterable<PropertySpec> = resourceSchema.getAllProperties()
        .filter { it.isAnnotationPresent(Relation::class) }
        .map { property ->
            val annotation = property.getAnnotationsByType(Relation::class).first()
            val propertyName = property.simpleName.asString()
            val relationName = annotation.name.ifEmpty { if (annotation.autoTransform) propertyName.toSnakeCase() else propertyName }
            logger.info("Generating attribute $propertyName of type ${property.type}")

            val relationType = property.type.resolve()
            val isToMany =
                relationType.declaration.qualifiedName?.asString() in listOf("kotlin.collections.List", "kotlin.collections.MutableList") && relationType.arguments.isNotEmpty()
            val relationResourceSchema = if (isToMany) {
                val typeArgument = relationType.arguments.first()
                typeArgument.type!!.resolve()
            } else relationType

            if (property.isMutable) {
                throw IllegalStateException("Relationship $propertyName of type ${resourceSchema.qualifiedName!!.asString()} is a var property. Relationships must be val.")
            }

            logger.info("Generating relationship $propertyName of type $resourceSchema")

            // Check if the type is a ResourceSchema
            val relationResourceSchemaResolved = resolver.getKotlinClassByName(relationResourceSchema.declaration.qualifiedName!!.asString())
            if (relationResourceSchemaResolved!!.annotations.none { it.annotationType.resolve().declaration.qualifiedName?.asString() == ResourceSchema::class.qualifiedName })
                throw IllegalStateException("$propertyName relationship type $relationResourceSchema of schema ${resourceSchema.qualifiedName!!.asString()} is not a ResourceSchema")

            val resourceTypeName = ClassName.bestGuess(relationResourceSchema.declaration.qualifiedName!!.asString().removeSuffix("Schema"))

            val realType = if (isToMany) {
                ClassName("kotlin.collections", if (annotation.mutable) "MutableList" else "List").parameterizedBy(resourceTypeName).copy(nullable = relationType.isMarkedNullable)
            } else {
                resourceTypeName
            }

//            val schemaType = if (isToMany) {
//                ClassName("kotlin.collections", "MutableList").parameterizedBy(relationResourceSchema.toTypeName()).copy(nullable = relationType.isMarkedNullable)
//            } else {
//                relationType.toTypeName()
//            }

            PropertySpec.builder(propertyName, realType)
                .mutable(annotation.mutable)
                .getter(
                    FunSpec.getterBuilder()
                        .let { builder ->
                            if (isToMany) {
                                builder.addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", "UNCHECKED_CAST").build())
                            } else builder
                        }
                        .addCode(
                            CodeBlock.builder()
                                .addNamed(
                                    "return if (%property:L.containsKey(%relationName:S) && (%property:L[%relationName:S] == null || %property:L[%relationName:S] is %relationTypeErased:T)) %property:L[%relationName:S] as %relationType:T else ".let {
                                        var s = it;
                                        if (!property.isAbstract()) {
                                            s += "%defaultValue:L as %relationType:T"
                                        } else {
                                            if (isToMany && !realType.isNullable) {
                                                s += "mutableListOf<%relationTypeInner:T>().%defaultValue:M()"
                                            } else {
                                                s += "%defaultValue:L"
                                            }
                                        }
                                        s
                                    },
                                    mapOf(
                                        "property" to "relationships",
                                        "relationName" to relationName,
                                        "relationTypeErased" to if (realType is ParameterizedTypeName) realType.copy(typeArguments = listOf(STAR)) else realType,
                                        "relationType" to realType,
                                        "defaultValue" to if (!property.isAbstract()) "super.$propertyName" else (if (isToMany) {
                                            if (realType.isNullable) "null" else MemberName("it.maicol07.spraypaintkt.extensions.", "trackChanges")
                                        } else {
                                            if (realType.isNullable) "null" else ("throw NoSuchElementException(\"$propertyName not found\")")
                                        }),
                                        "relationTypeInner" to resourceTypeName
                                    ),
                                )
                                .build()
                        )
                        .build()
                )
                .let {
                    if (annotation.mutable) {
                        it.setter(
                            FunSpec.setterBuilder()
                                .addParameter("value", realType)
                                .addCode(
                                    CodeBlock.builder()
                                        .addStatement("relationships[%S] = value", relationName)
                                        .build()
                                )
                                .build()
                        )
                    } else it
                }
                .addModifiers(KModifier.OVERRIDE)
                .build()
        }
        .asIterable()

    private fun getDefaultConfig(resolver: Resolver): KSClassDeclaration? =
        resolver.getSymbolsWithAnnotation(DefaultInstance::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .filter { declaration -> declaration.superTypes.any { it.resolve().declaration.qualifiedName?.asString() == JsonApiConfig::class.qualifiedName } }
            .firstOrNull()

    private fun generateScopeFunctions(resourceType: TypeName): List<FunSpec> {
        val methods = Scope::class.declaredMemberFunctions
        logger.info("Generating scope functions for $resourceType: ${methods.joinToString { it.name }}")
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
            .filter { it.hasAnnotation<Scope.ScopeMethod>() }
            .onEach { logger.info("Generating method ${it.name}") }
            .map { method ->
                logger.info("Generating method ${method.name}")
                val params = method.parameters.filter { it.name != null }
                val returnType =
                    if (method.returnType.classifier is KClass<*> && method.returnType.arguments.firstOrNull()?.type.toString() == "R") {
                        (method.returnType.classifier as KClass<*>).asTypeName()
                            .parameterizedBy(resourceType)
                    } else {
                        method.returnType.asTypeName()
                    }
                val modifiers = mutableListOf<KModifier>().apply {
                    if (method.isSuspend) add(KModifier.SUSPEND)
                    if (method.isInline) add(KModifier.INLINE)
                }

                FunSpec.builder(method.name)
                    .addModifiers(modifiers)
                    .addParameters(params.map {
                        ParameterSpec.builder(it.name!!, (if (it.isVararg) it.type.arguments.first().type else it.type)!!.asTypeName())
                            .addModifiers(mutableListOf<KModifier>().apply {
                                if (it.isVararg) add(KModifier.VARARG)
                            })
                            .build()
                    })
                    .returns(returnType)
                    .addStatement(
                        "return scope().%L(%L)",
                        method.name,
                        params.joinToString(", ") { (if (it.isVararg) "*" else "") + it.name!! })
                    .build()
            }
    }

    override fun finish() {
        super.finish()
        for (file in filesToWrite) {
            logger.info("Writing file ${file.name}")
            file.writeTo(codeGenerator, Dependencies(true, *fileDependencies.toTypedArray()))
        }
    }
}