package it.maicol07.spraypaintkt_processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.auto.service.AutoService

@AutoService(SymbolProcessorProvider::class)
class ResourceSchemaAnnotationProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ResourceSchemaProcessor(
            logger = environment.logger,
            codeGenerator = environment.codeGenerator
        )
    }
}