package it.maicol07.spraypaintkt.util

import it.maicol07.spraypaintkt.JsonApiResource
import it.maicol07.spraypaintkt.JsonApiSingleResponse
import it.maicol07.spraypaintkt.Resource
import it.maicol07.spraypaintkt.ResourceRegistry
import it.maicol07.spraypaintkt.extensions.trackChanges

/**
 * Deserializer for JSON:API resources.
 */
class Deserializer {
    /**
     * Cache for deserialized resources.
     */
    private val cache = mutableMapOf<Pair<String, String>, Resource>()

    /**
     * Deserializes a JSON:API response into a [Resource] object.
     *
     * @param jsonApiData The JSON:API data to deserialize.
     * @param included The included resources.
     *
     * @return The deserialized [Resource] object.
     */
    fun deserialize(jsonApiData: JsonApiResource, included: List<JsonApiResource>): Resource {
        val type = jsonApiData.type
        val id = jsonApiData.id
        val model = cache.getOrElse(Pair(type, id)) {
            val resource = ResourceRegistry.createInstance(type)
            deserializeToResource(resource, jsonApiData, included)
            resource
        }
        return model
    }

    /**
     * Deserializes a JSON:API response into a [Resource] object.
     *
     * @param jsonApiResponse The JSON:API response to deserialize.
     *
     * @return The deserialized [Resource] object.
     */
    fun deserialize(jsonApiResponse: JsonApiSingleResponse): Resource {
        return deserialize(jsonApiResponse.data!!, jsonApiResponse.included)
    }

    /**
     * Deserializes a JSON:API resource into a [Resource] object.
     *
     * @param jsonApiResource The JSON:API resource to deserialize.
     *
     * @return The deserialized [Resource] object.
     */
    fun deserialize(jsonApiResource: JsonApiResource): Resource {
        return deserialize(jsonApiResource, listOf())
    }

    /**
     * Deserializes a JSON:API resource into a [Resource] object.
     *
     * @param resource The JSON:API resource to deserialize into.
     * @param jsonApiResponse The JSON:API response to deserialize.
     *
     * @return The deserialized [Resource] object.
     */
    fun <R: Resource> deserializeToResource(resource: R, jsonApiResponse: JsonApiSingleResponse): R {
        return deserializeToResource(resource, jsonApiResponse.data!!, jsonApiResponse.included)
    }

    /**
     * Deserializes a JSON:API resource into a [Resource] object.
     *
     * @param resource The [Resource] object to deserialize into.
     * @param datum The JSON:API resource to deserialize.
     * @param included The included resources.
     *
     * @return The updated [Resource] object.
     */
    fun <R: Resource> deserializeToResource(resource: R, datum: JsonApiResource, included: List<JsonApiResource>): R {
//        Logger.d("Deserializer") { "Deserializing ${model.type} with id ${datum.id}" }
        resource.id = datum.id
        resource.isPersisted = true

        resource.attributes.putAll(datum.attributes.map { (key, value) ->
            when (value) {
                is List<*> -> key to value.toMutableList().trackChanges { _, list ->
                    resource.attributes.trackChange(key, null, list)
                }
                is Map<*, *> -> key to value.toMutableMap().trackChanges { _, _, map ->
                    resource.attributes.trackChange(key, null, map)
                }
                else -> key to value
            }
        })
        resource.attributes.clearChanges()

        cache[Pair(datum.type, datum.id)] = resource

        for ((key, relationship) in datum.relationships) {
            val relationData = relationship?.data
            if (relationData != null) {
                val relatedResources = mutableListOf<Resource>()

                for (relationshipData in relationData) {
                    val type = relationshipData.type
                    val id = relationshipData.id
                    val related = included.find { it.type == type && it.id == id }
                    if (related != null) {
                        val cached = cache.getOrElse(Pair(type, id)) {
                            val resource = ResourceRegistry.createInstance(type)
                            deserializeToResource(resource, related, included)
                            resource
                        }
                        relatedResources.add(cached)
                    }
                }

                if (relatedResources.size == 1 && relationship.isSingle) {
                    resource.relationships[key] = relatedResources.first()
                } else {
                    resource.relationships[key] = relatedResources.trackChanges { _, list ->
                        resource.relationships.trackChange(key, null, list)
                    }
                }
            }
        }
        resource.relationships.clearChanges()

        resource.links.putAll(datum.links)
        resource.meta.putAll(datum.meta)

        return resource
    }
}