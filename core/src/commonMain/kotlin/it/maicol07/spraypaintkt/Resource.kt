package it.maicol07.spraypaintkt

import it.maicol07.spraypaintkt.extensions.toJsonElement
import it.maicol07.spraypaintkt.extensions.trackChanges
import it.maicol07.spraypaintkt.interfaces.JsonApiConfig
import it.maicol07.spraypaintkt.util.Deserializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * A JSON:API resource.
 *
 * @param resourceType The type of the resource.
 * @param endpoint The endpoint of the resource (if different from the type).
 */
abstract class Resource {
    interface Companion<R : Resource> {
        val resourceType: String
        val endpoint: String
        val config: JsonApiConfig
        val factory: () -> R
        val schema: KClass<*>

        fun urlForResource(resource: Resource? = null, id: String? = null): String {
            return listOf(config.baseUrl, config.apiNamespace, endpoint, id ?: resource?.id ?: "")
                .filter { it.isNotEmpty() }
                .joinToString("/") { it.trim('/', '\\') }
        }
    }

    abstract val companion: Companion<out Resource>

    /** The ID of the resource. */
    var id: String? = null

    /** Whether the resource is persisted. */
    var isPersisted = false

    /** The attributes of the resource. */
    val attributes = mutableMapOf<String, Any?>().trackChanges()

    /** The relationships of the resource. */
    val relationships = mutableMapOf<String, Any?>().trackChanges()

    /** The meta of the resource. */
    val meta = mutableMapOf<String, Any?>()

    /** The links of the resource. */
    val links = mutableMapOf<String, Any?>()

    /** The type of the resource. */
    val type: String by lazy { companion.resourceType }

    /**
     * Deserialize the resource from a JSON:API response.
     *
     * @param jsonApiData The JSON:API data.
     * @param included The included resources.
     */
    fun fromJsonApi(jsonApiData: JsonApiResource, included: List<JsonApiResource>) {
        Deserializer().deserialize(this, jsonApiData, included)
    }

    /**
     * Serialize the resource to a JSON:API object.
     */
    fun toJsonApi(onlyDirty: Boolean = false): Map<String, Any?> {
        val data = mutableMapOf<String, Any?>("type" to type)
        if (id != null) {
            data["id"] = id
        }
        data["attributes"] = if (onlyDirty) attributes.getChanges() else attributes

        val relationships = mutableMapOf<String, Any>()
        for ((key, value) in (if (onlyDirty) this.relationships.getChanges() else this.relationships)) {
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
    fun toJsonApiString(from: Json = Json.Default, builder: JsonBuilder.() -> Unit = {}, onlyDirty: Boolean = false): String {
        @Suppress("JSON_FORMAT_REDUNDANT")
        return Json(from, builder).encodeToString(toJsonApi(onlyDirty).toJsonElement())
    }

    fun toUrl(): String = companion.urlForResource(this)
}