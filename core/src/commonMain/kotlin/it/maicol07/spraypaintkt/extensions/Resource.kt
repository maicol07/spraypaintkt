package it.maicol07.spraypaintkt.extensions

import it.maicol07.spraypaintkt.JsonApiException
import it.maicol07.spraypaintkt.JsonApiResource
import it.maicol07.spraypaintkt.JsonApiSingleResponse
import it.maicol07.spraypaintkt.Resource
import it.maicol07.spraypaintkt.util.Deserializer
import kotlinx.serialization.json.Json


/**
 * Save the resource to the server.
 *
 * @return `true` if the resource was saved successfully. `false` otherwise.
 */
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
        fromJsonApiResponse(JsonApiSingleResponse.fromJsonApiString(response.body))
    }

    return true
}

/**
 * Destroy a resource from the server.
 *
 * @return `true` if the resource was destroyed successfully. `false` otherwise.
 */
suspend fun <R: Resource> R.destroy(): Boolean {
    val url = companion.urlForResource(this)
    val response = companion.config.httpClient.delete(url)
    if (response.statusCode !in listOf(200, 204)) {
        throw JsonApiException(response.statusCode, response.body)
    }
    return true
}
