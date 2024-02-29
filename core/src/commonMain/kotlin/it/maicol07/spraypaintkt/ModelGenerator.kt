package it.maicol07.spraypaintkt

import eu.prepsoil.app.utils.jsonapiclient.Resource
import kotlin.reflect.KClass

interface ModelGenerator {
    fun <R: Resource> generate(clazz: KClass<R>): R
}