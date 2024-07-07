package it.maicol07.spraypaintkt.extensions

import it.maicol07.spraypaintkt.JsonApiException
import it.maicol07.spraypaintkt.JsonApiResource
import it.maicol07.spraypaintkt.Resource
import kotlinx.serialization.json.Json


/**
 * Save a resource to the server.
 *
 * @param resource The resource to save.
 */
@Suppress("UNCHECKED_CAST")
suspend fun <R: Resource> R.save(): Boolean {
    val url = companion.urlForResource(this)
    val response = if (isPersisted) {
        companion.config.httpClient.patch(url, toJsonApiString(onlyDirty = true))
    } else {
        companion.config.httpClient.post(url, toJsonApiString())
    }
    if (response.statusCode !in 200..204) {
        throw JsonApiException(response.statusCode, response.body)
    }

    if (!isPersisted && response.statusCode == 201) {
        val jsonApiResponse = Json.parseToJsonElement(response.body).extractedContent as JsonObjectMap?
            ?: throw JsonApiException(response.statusCode, response.body)
        fromJsonApi(JsonApiResource(jsonApiResponse["data"] as JsonObjectMap), emptyList())
    }

    return true
}

/**
 * Destroy a resource from the server.
 *
 * @param resource The resource to destroy.
 */
suspend fun <R: Resource> R.destroy(resource: R): Boolean {
    val url = companion.urlForResource(resource, resource.id)
    val response = companion.config.httpClient.delete(url)
    if (response.statusCode !in listOf(200, 204)) {
        throw JsonApiException(response.statusCode, response.body)
    }
    return true
}
