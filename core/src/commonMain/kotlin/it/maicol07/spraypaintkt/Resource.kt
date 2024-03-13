package it.maicol07.spraypaintkt

import it.maicol07.spraypaintkt.extensions.toJsonElement
import it.maicol07.spraypaintkt.util.Deserializer
import it.maicol07.spraypaintkt.util.pluralize
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class Resource(
    private val resourceType: String? = null,
    val endpoint: String? = null
) {
    var id: String? = null
    var isPersisted = false

    val attributes = object : MutableMap<String, Any?> by mutableMapOf() {}
    val relationships: MutableMap<String, Any> = mutableMapOf()
    val meta: MutableMap<String, Any?> = mutableMapOf()
    val links: MutableMap<String, Any?> = mutableMapOf()

    val type: String
        get() = resourceType ?: this::class.simpleName?.lowercase()?.pluralize() ?: ""

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
    @Suppress("ktPropBy")
    protected fun <R: Resource, RL: List<R>, RO: Resource> RO.hasManyRelationship(name: String) = object : ReadWriteProperty<RO, RL> {
        override fun getValue(thisRef: RO, property: KProperty<*>): RL {
            @Suppress("UNCHECKED_CAST")
            return relationships[name] as RL
        }

        override fun setValue(thisRef: RO, property: KProperty<*>, value: RL) {
            relationships[name] = value as Any
        }
    }

    fun toJsonApi(): Map<String, Any?> {
        val data = mutableMapOf<String, Any?>("type" to type,)
        if (id != null) {
            data["id"] = id
        }
        data["attributes"] = attributes

        val relationships = mutableMapOf<String, Any>()
        for ((key, value) in this.relationships) {
            @Suppress("UNCHECKED_CAST")
            val rel = relationships.getOrPut(key) { mutableMapOf<String, Map<String, Any>>() } as MutableMap<String, Any>
            if (value is Resource) {
                rel["data"] = mapOf(
                    "type" to value.type,
                    "id" to value.id!!,
                )
            } else if (value is List<*>) {
                rel["data"] = value.map {
                    if (it is Resource) {
                        mapOf(
                            "type" to it.type,
                            "id" to it.id,
                        )
                    } else {
                        null
                    }
                }.filterNotNull()
            }
        }
        data["relationships"] = relationships

        return mapOf("data" to data)
    }

    fun toJsonApiString(from: Json = Json.Default, builder: JsonBuilder.() -> Unit = {}): String {
        @Suppress("JSON_FORMAT_REDUNDANT")
        return Json(from, builder).encodeToString(toJsonApi().toJsonElement())
    }
}