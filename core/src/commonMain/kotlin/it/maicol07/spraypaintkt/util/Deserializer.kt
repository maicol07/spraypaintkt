package it.maicol07.spraypaintkt.util

import it.maicol07.spraypaintkt.JsonApiResource
import it.maicol07.spraypaintkt.Resource

/**
 * Deserializer for JSON:API resources.
 *
 * @param typeRegistry The type registry to use.
 */
class Deserializer(private val typeRegistry: Map<String, () -> Resource>) {
    /**
     * Cache for deserialized resources.
     */
    private val cache = mutableMapOf<Pair<String, String>, Resource>()

    /**
     * Deserializes a JSON:API resource into a [Resource] object.
     *
     * @param model The [Resource] object to deserialize into.
     * @param datum The JSON:API resource to deserialize.
     * @param included The included resources.
     * @return The deserialized [Resource] object.
     */
    fun <R: Resource> deserialize(model: R, datum: JsonApiResource, included: List<JsonApiResource>): R {
        // already

//        Logger.d("Deserializer") { "Deserializing ${model.type} with id ${datum.id}" }
        model.id = datum.id
        model.isPersisted = true

        model.attributes.putAll(datum.attributes)

        for ((key, relationship) in datum.relationships) {
            val relationData = relationship?.data
            if (relationData != null) {
                val relatedResources = mutableListOf<Resource>()

                for (relationshipData in relationData) {
                    val type = relationshipData.type
                    val id = relationshipData.id
                    val related = included.find { it.type == type && it.id == id }
                    if (related != null) {
                        val cached = cache.getOrPut(Pair(type, id)) {
                            val resource = typeRegistry[type]?.invoke() ?: throw RuntimeException("No type registered for $type")
                            deserialize(resource, related, included)
                            resource
                        }
                        relatedResources.add(cached)
                    }
                }

                if (relatedResources.size == 1 && relationship.isSingle) {
                    model.relationships[key] = relatedResources.first()
                } else {
                    model.relationships[key] = relatedResources
                }
            }
        }

        model.links.putAll(datum.links)
        model.meta.putAll(datum.meta)

        return model
    }
}