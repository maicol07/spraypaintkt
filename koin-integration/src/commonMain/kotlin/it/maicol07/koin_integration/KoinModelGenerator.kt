package it.maicol07.koin_integration

import it.maicol07.spraypaintkt.Resource
import it.maicol07.spraypaintkt.ModelGenerator
import org.koin.core.component.KoinComponent
import kotlin.reflect.KClass

class KoinModelGenerator : ModelGenerator, KoinComponent {
    override fun <R : Resource> generate(clazz: KClass<R>): R {
        return getKoin().get(clazz)
    }
}