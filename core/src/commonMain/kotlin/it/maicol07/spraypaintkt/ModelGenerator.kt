package it.maicol07.spraypaintkt

import kotlin.reflect.KClass

interface ModelGenerator {
    fun <R: Resource> generate(clazz: KClass<R>): R
}