@file:Suppress("UNCHECKED_CAST")

package it.maicol07.spraypaintkt

/**
 * Base fields for a JSON:API response
 *
 * @param response The response map.
 */
abstract class JsonApiResponse(
    response: Map<String, Any>,
): Map<String, Any?> by response {
    /** The top-level meta of the response. */
    val meta = response.getOrElse("meta") { emptyMap<String, Any>() } as Map<String, Any>
    /** The top-level links of the response. */
    val links: Map<String, Any>? by response
    /** The included resources. */
    val included: List<JsonApiResource> by lazy {
        (response.getOrElse("included") { emptyList<Map<String, Any>>() } as List<Map<String, Any>>).map { JsonApiResource(it) }
    }
}

/**
 * A single JSON:API response
 *
 * @param response The response map.
 */
class JsonApiSingleResponse(
    private val response: Map<String, Any>,
): JsonApiResponse(response) {
    /** The data of the response. */
    val data: JsonApiResource?
        get() {
            var data = response["data"]
            if (data == null) {
                return data
            }
            if (data is List<*>) {
                data = data.firstOrNull()
            }
            return JsonApiResource(data as Map<String, Any>? ?: emptyMap())
        }
}

/**
 * A collection JSON:API response
 *
 * @param response The response map.
 */
class JsonApiCollectionResponse(
    response: Map<String, Any>,
): JsonApiResponse(response) {
    /** The data of the response. */
    val data = (response["data"] as List<Map<String, Any>>).map { JsonApiResource(it) }
}

/**
 * A JSON:API resource
 *
 * @param response The response map.
 */
class JsonApiResource(
    response: Map<String, Any>,
): Map<String, Any?> by response {
    /** The ID of the resource. */
    val id: String by response
    /** The type of the resource. */
    val type: String by response
    /** The attributes of the resource. */
    val attributes: Map<String, Any?> by response
    /** The relationships of the resource. */
    val relationships: Map<String, JsonApiRelationship?> by lazy {
        response["relationships"]?.let {
            (it as Map<String, Map<String, Any>>).mapValues { JsonApiRelationship(it.value) }
        } ?: emptyMap()
    }
    /** The links of the resource. */
    val links: Map<String, Any?> by lazy { response.getOrElse("links") { emptyMap<String, Any>() } as Map<String, Any> }
    /** The meta of the resource. */
    val meta: Map<String, Any?> by lazy { response.getOrElse("meta") { emptyMap<String, Any>() } as Map<String, Any> }
}

/**
 * A JSON:API relationship
 *
 * @param response The response map.
 */
class JsonApiRelationship(
    response: Map<String, Any>,
): Map<String, Any?> by response {
    /** The data of the relationship. */
    val data: List<JsonApiRelationshipData> by lazy {
        val list = mutableListOf<JsonApiRelationshipData>()
        if (response["data"] is Map<*, *>) {
            list.add(JsonApiRelationshipData(response["data"] as Map<String, Any>))
        }

        if (response["data"] is List<*>) {
            list.addAll((response["data"] as List<Map<String, Any>>).map { JsonApiRelationshipData(it) })
        }

        list
    }
    /** If the relationship is a single relationship. */
    val isSingle: Boolean by lazy { response["data"] is Map<*, *> || response["data"] == null }
    /** The links of the relationship. */
    val links = response.getOrElse("links") { null } as? JsonApiRelationshipLinks
    /** The meta of the relationship. */
    val meta = response.getOrElse("meta") { null } as? Map<String, Any>
}

/**
 * A JSON:API relationship data
 *
 * @param response The response map.
 */
class JsonApiRelationshipData(
    response: Map<String, Any>,
): Map<String, Any?> by response {
    /** The ID of the relationship. */
    val id: String by response
    /** The type of the relationship. */
    val type: String by response
}

/**
 * A JSON:API relationship links
 *
 * @param response The response map.
 */
class JsonApiRelationshipLinks(
    response: Map<String, Any>,
): Map<String, Any?> by response {
    /** The self link of the relationship. */
    val self: String by response
    /** The related link of the relationship. */
    val related: String by response
}