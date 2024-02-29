package eu.prepsoil.app.utils.jsonapiclient

import eu.prepsoil.app.utils.jsonapiclient.util.Deserializer
import it.maicol07.spraypaintkt.util.pluralize
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class Resource(
    private val resourceType: String? = null,
    val endpoint: String? = null
) {
    var id: String? = null
    var tempId = ""

    val attributes = object : MutableMap<String, Any?> by mutableMapOf() {}
    val relationships: MutableMap<String, Any> = mutableMapOf()
    val meta: MutableMap<String, Any> = mutableMapOf()
    val type: String
        get() = resourceType ?: this::class.simpleName?.pluralize() ?: ""

    fun fromJsonApi(jsonApiData: JsonApiResource, included: List<JsonApiResource>, deserializer: Deserializer) {
        deserializer.deserialize(this, jsonApiData, included)
    }

    protected fun <T> attribute(name: String) = lazy { attributes[name]?.let {
        @Suppress("UNCHECKED_CAST")
        it as T
    } }
    @Suppress("ktPropBy")
    protected fun <R: Resource, RO: Resource> RO.relationship(name: String) = object : ReadWriteProperty<RO, R?> {
        override fun getValue(thisRef: RO, property: KProperty<*>): R? {
            @Suppress("UNCHECKED_CAST")
            return relationships[name] as R?
        }

        override fun setValue(thisRef: RO, property: KProperty<*>, value: R?) {
            relationships[name] = value as Any
        }
    }
}