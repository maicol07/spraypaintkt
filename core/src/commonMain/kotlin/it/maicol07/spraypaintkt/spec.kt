@file:Suppress("UNCHECKED_CAST")

package it.maicol07.spraypaintkt

abstract class JsonApiResponse(
    response: Map<String, Any>,
): Map<String, Any?> by response {
    val meta = response.getOrElse("meta") { emptyMap<String, Any>() } as Map<String, Any>
    val links: Map<String, Any>? by response
    val included: List<JsonApiResource> by lazy {
        (response.getOrElse("included") { emptyList<Map<String, Any>>() } as List<Map<String, Any>>).map { JsonApiResource(it) }
    }
}

class JsonApiSingleResponse(
    private val response: Map<String, Any>,
): JsonApiResponse(response) {
    val data: JsonApiResource?
        get() {
            var data = response["data"]
            if (data == null) {
                return data
            }
            if (data is List<*>) {
                data = data.firstOrNull()
            }
            return JsonApiResource(data as Map<String, Any>)
        }
}

class JsonApiCollectionResponse(
    response: Map<String, Any>,
): JsonApiResponse(response) {
    val data = (response["data"] as List<Map<String, Any>>).map { JsonApiResource(it) }
}

class JsonApiResource(
    response: Map<String, Any>,
): Map<String, Any?> by response {
    val id: String by response
    val type: String by response
    val attributes: Map<String, Any?> by response
    val relationships: Map<String, JsonApiRelationship?> by lazy {
        response["relationships"]?.let {
            (it as Map<String, Map<String, Any>>).mapValues { JsonApiRelationship(it.value) }
        } ?: emptyMap()
    }
    val links: Map<String, Any?> by lazy { response.getOrElse("links") { emptyMap<String, Any>() } as Map<String, Any> }
    val meta: Map<String, Any?> by lazy { response.getOrElse("meta") { emptyMap<String, Any>() } as Map<String, Any> }
}

class JsonApiRelationship(
    response: Map<String, Any>,
): Map<String, Any?> by response {
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
    val isSingle: Boolean by lazy { response["data"] is Map<*, *> }
    val links = response.getOrElse("links") { null } as? JsonApiRelationshipLinks
    val meta = response.getOrElse("meta") { null } as? Map<String, Any>
}

class JsonApiRelationshipData(
    response: Map<String, Any>,
): Map<String, Any?> by response {
    val id: String by response
    val type: String by response
}

class JsonApiRelationshipLinks(
    response: Map<String, Any>,
): Map<String, Any?> by response {
    val self: String by response
    val related: String by response
}