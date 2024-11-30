package it.maicol07.spraypaintkt

import it.maicol07.spraypaintkt.extensions.DirtyMap
import it.maicol07.spraypaintkt.extensions.toJsonElement
import it.maicol07.spraypaintkt.interfaces.JsonApiConfig
import it.maicol07.spraypaintkt.util.Deserializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlin.reflect.KClass

/**
 * A JSON:API resource.
 */
@Serializable(with = ResourceSerializer::class)
interface Resource {
    /**
     * A companion object for the resource.
     *
     * @param R The type of the resource.
     */
    interface CompanionObj<R : Resource> {
        /** The type of the resource. */
        val resourceType: String

        /** The endpoint of the resource. */
        val endpoint: String

        /** The configuration of the resource. */
        val config: JsonApiConfig

        /** The factory for the resource. */
        val factory: () -> R

        /** The schema of the resource. */
        val schema: KClass<*>

        /**
         * Create a new instance of the resource.
         *
         * @return The new instance.
         */
        fun urlForResource(resource: Resource? = null, id: String? = null): String {
            return listOf(config.baseUrl, config.apiNamespace, endpoint, id ?: resource?.id ?: "")
                .filter { it.isNotEmpty() }
                .joinToString("/") { it.trim('/', '\\') }
        }

        /**
         * Deserialize the resource from a JSON:API response.
         *
         * @param jsonApiData The JSON:API data.
         * @param included The included resources.
         */
        fun fromJsonApi(jsonApiData: JsonApiResource, included: List<JsonApiResource>) {
            Deserializer().deserialize(jsonApiData, included)
        }

        /**
         * Deserialize the resource from a JSON:API response.
         *
         * @param jsonApiResponse The JSON:API response.
         */
        fun fromJsonApi(jsonApiResponse: JsonApiSingleResponse) {
            Deserializer().deserialize(jsonApiResponse)
        }
    }

    /** The companion object of the resource. */
    val companion: CompanionObj<out Resource>

    /** The ID of the resource. */
    var id: String?

    /** Whether the resource is persisted. */
    var isPersisted: Boolean

    /** The attributes of the resource. */
    val attributes: DirtyMap<String, Any?>

    /** The relationships of the resource. */
    val relationships: DirtyMap<String, Any?>

    /** The meta of the resource. */
    val meta: MutableMap<String, Any?>

    /** The links of the resource. */
    val links: MutableMap<String, Any?>

    /** The type of the resource. */
    val type: String

    /**
     * Serialize the resource to a JSON:API object.
     */
    fun toJsonApi(onlyDirty: Boolean = false): Map<String, Any?> {
        val data = mutableMapOf<String, Any?>("type" to type)
        if (id != null) {
            data["id"] = id
        }
        data["attributes"] = if (onlyDirty) attributes.getChanges() else attributes

        val included = mutableSetOf<Map<String, Any?>>()
        val relationships = mutableMapOf<String, Any>()
        for ((key, value) in (if (onlyDirty) this.relationships.getChanges() else this.relationships)) {
            @Suppress("UNCHECKED_CAST")
            val rel = relationships.getOrPut(key) { mutableMapOf<String, Map<String, Any>>() } as MutableMap<String, Any>
            val valueList = if (value is List<*>) value else listOf(value)
            rel["data"] = valueList.map {
                if (it is Resource) mapOf(
                    "type" to it.type,
                    "id" to it.id,
                ) else null
            }.filterNotNull().let { if (it.count() == 1) it.first() else it }
            included.addAll(valueList.mapNotNull {
                if (it !is Resource) return@mapNotNull null
                val resJsonApi = it.toJsonApi()
                @Suppress("UNCHECKED_CAST")
                included.addAll(resJsonApi["included"] as Collection<Map<String, Any?>>)
                @Suppress("UNCHECKED_CAST")
                resJsonApi["data"] as Map<String, Any?>
            })
        }
        data["relationships"] = relationships

        return mapOf("data" to data, "included" to included)
    }

    /**
     * Serialize the resource to a JSON:API string.
     */
    fun toJsonApiString(from: Json = Json.Default, builder: JsonBuilder.() -> Unit = {}, onlyDirty: Boolean = false): String {
        @Suppress("JSON_FORMAT_REDUNDANT")
        return Json(from, builder).encodeToString(toJsonApi(onlyDirty).toJsonElement())
    }


    /**
     * Deserialize the resource from a JSON:API response.
     *
     * @param jsonApiData The JSON:API data.
     * @param included The included resources.
     */
    fun fromJsonApi(jsonApiData: JsonApiResource, included: List<JsonApiResource> = listOf()) {
        Deserializer().deserializeToResource(this, jsonApiData, included)
    }
    /**
     * Deserialize the resource from a JSON:API response.
     *
     * @param jsonApiData The JSON:API data.
     * @param included The included resources.
     */
    fun fromJsonApiResponse(jsonApiResponse: JsonApiSingleResponse) {
        Deserializer().deserializeToResource(this, jsonApiResponse)
    }

    /**
     * Convert the resource to a URL.
     */
    fun toUrl(): String = companion.urlForResource(this)
}