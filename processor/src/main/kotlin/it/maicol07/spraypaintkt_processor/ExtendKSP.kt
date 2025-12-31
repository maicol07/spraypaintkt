/**
 * Workaround extensions for KSP to get annotation values.
 * @see https://github.com/google/ksp/issues/2356
 * @see https://github.com/DatL4g/Native-Kommons/commit/e450e31ec335cfe52935bfef47ab2e42b0be99b7#diff-5605daa8ad9378c9e5156308c03e353856f4110247923106cdcaed6ff2df6d4a
 * @source https://github.com/DatL4g/Native-Kommons/blob/master/ksp/src/jvmMain/kotlin/dev/datlag/nkommons/common/ExtendKSP.kt
 */
package it.maicol07.spraypaintkt_processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation

@OptIn(KspExperimental::class)
inline fun <reified A : Annotation> KSAnnotated.findAnnotation() = this.annotations.firstOrNull {
    it.shortName.getShortName() == A::class.simpleName && it.annotationType.resolve().declaration.qualifiedName?.asString() == A::class.qualifiedName
}

inline fun <reified T> KSAnnotation.getArgumentValueByName(name: String) = this.arguments.firstOrNull {
    it.name?.asString() == name && it.value != null && it.value is T
}?.value as? T

inline fun <reified A : Annotation, reified T> KSAnnotated.getAnnotationValue(
    name: String,
    useAnnotationDefaultValue: Boolean = true
): T? =
    findAnnotation<A>()?.getArgumentValueByName<T>(name) ?: if (useAnnotationDefaultValue) {
        A::class.java.methods.firstOrNull { it.name == name }?.defaultValue?.let {
            if (it is Class<*>) it.kotlin else it
        } as? T
    } else {
        null
    }
