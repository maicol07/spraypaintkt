package it.maicol07.spraypaintkt_processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.*
import it.maicol07.spraypaintkt_annotation.Model

/** Processes [DtoToModel] annotations in order to create 'model' representations of annotated classes. */
class DtoToModelProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotationName: String = Model::class.qualifiedName
            ?: throw IllegalStateException("Annotation name could not be retrieved.")

        resolver.getSymbolsWithAnnotation(annotationName)
            .filterIsInstance(KSClassDeclaration::class.java)
            .forEach { annotated ->
                println(annotated.simpleName.asString())
//                annotated.getAllProperties().first().
            }

        return emptyList()
    }
}