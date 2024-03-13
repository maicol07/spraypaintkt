package it.maicol07.spraypaintkt

import it.maicol07.spraypaintkt.extensions.toJsonElement
import it.maicol07.spraypaintkt.util.Deserializer
import it.maicol07.spraypaintkt.util.pluralize
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A JSON:API resource.
 *
 * @param resourceType The type of the resource.
 * @param endpoint The endpoint of the resource (if different from the type).
 */
abstract class Resource(
    private val resourceType: String? = null,
    val endpoint: String? = null
) {
    /** The ID of the resource. */
    var id: String? = null
    /** Whether the resource is persisted. */
    var isPersisted = false

    /** The attributes of the resource. */
    val attributes = object : MutableMap<String, Any?> by mutableMapOf() {}
    /** The relationships of the resource. */
    val relationships: MutableMap<String, Any> = mutableMapOf()
    /** The meta of the resource. */
    val meta: MutableMap<String, Any?> = mutableMapOf()
    /** The links of the resource. */
    val links: MutableMap<String, Any?> = mutableMapOf()

    /** The type of the resource. */
    val type: String
        get() = resourceType ?: this::class.simpleName?.lowercase()?.pluralize() ?: ""

    /**
     * Deserialize the resource from a JSON:API response.
     *
     * @param jsonApiData The JSON:API data.
     * @param included The included resources.
     * @param deserializer The deserializer to use.
     */
    fun fromJsonApi(jsonApiData: JsonApiResource, included: List<JsonApiResource>, deserializer: Deserializer) {
        deserializer.deserialize(this, jsonApiData, included)
    }

    /**
     * Delegate for attributes. Let you specify the name of the attribute to delegate to.
     *
     * @param name The name of the attribute.
     */
    protected fun <T> attribute(name: String) = lazy { attributes[name]?.let {
        @Suppress("UNCHECKED_CAST")
        it as T
    } }

    /**
     * Delegate for relationships. Let you specify the name of the relationship to delegate to.
     *
     * @param name The name of the relationship.
     * @param R The type of the relationship.
     * @param RO The type of the resource.
     */
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

    /**
     * Delegate for has-many relationships. Let you specify the name of the relationship to delegate to.
     *
     * @param name The name of the relationship.
     * @param R The type of the relationship.
     * @param RL The type of the list of relationships.
     * @param RO The type of the resource.
     */
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

    /**
     * Serialize the resource to a JSON:API object.
     */
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

    /**
     * Serialize the resource to a JSON:API string.
     */
    fun toJsonApiString(from: Json = Json.Default, builder: JsonBuilder.() -> Unit = {}): String {
        @Suppress("JSON_FORMAT_REDUNDANT")
        return Json(from, builder).encodeToString(toJsonApi().toJsonElement())
    }
}