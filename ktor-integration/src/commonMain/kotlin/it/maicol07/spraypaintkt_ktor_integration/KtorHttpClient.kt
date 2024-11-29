package it.maicol07.spraypaintkt_ktor_integration

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import it.maicol07.spraypaintkt.interfaces.HttpClientResponse

@Suppress("MissingTestClass")
/**
 * A Ktor implementation of the [HttpClient] interface.
 *
 * @param engineFactory The engine factory to use for the HTTP client.
 * @param httpClientOptions The options to pass to the HTTP client.
 * @param httpClient The HTTP client to use. [engineFactory] and [httpClientOptions] will be ignored if this is provided. [engineFactory] will not be used if is null.
 */
class KtorHttpClient(
    engineFactory: HttpClientEngineFactory<*>? = null,
    httpClientOptions: HttpClientConfig<*>.() -> Unit = {
        defaultRequest {
            accept(VndApiJson)
            contentType(VndApiJson)
        }
    },
    val httpClient: HttpClient = if (engineFactory == null) HttpClient(httpClientOptions) else HttpClient(engineFactory, httpClientOptions)
): it.maicol07.spraypaintkt.interfaces.HttpClient {
    companion object {
        val VndApiJson = ContentType("application", "vnd.api+json")
    }
    override suspend fun get(url: String, parameters: Map<String, String>): HttpClientResponse {
        val response = httpClient.get(url) {
            for ((key, value) in parameters) {
                parameter(key, value)
            }
            accept(VndApiJson)
        }

        val responseBody = response.bodyAsText()
        return object : HttpClientResponse {
            override val statusCode: Int = response.status.value
            override val body: String = responseBody
        }
    }

    override suspend fun patch(
        url: String,
        body: String,
        parameters: Map<String, String>
    ): HttpClientResponse {
        val response = httpClient.patch(url) {
            for ((key, value) in parameters) {
                parameter(key, value)
            }
            accept(VndApiJson)
            contentType(VndApiJson)
            setBody(body)
        }

        val responseBody = response.bodyAsText()
        return object : HttpClientResponse {
            override val statusCode: Int = response.status.value
            override val body: String = responseBody
        }
    }

    override suspend fun post(
        url: String,
        body: String,
        parameters: Map<String, String>
    ): HttpClientResponse {
        val response = httpClient.post(url) {
            for ((key, value) in parameters) {
                parameter(key, value)
            }
            accept(VndApiJson)
            contentType(VndApiJson)
            setBody(body)
        }

        val responseBody = response.bodyAsText()
        return object : HttpClientResponse {
            override val statusCode: Int = response.status.value
            override val body: String = responseBody
        }
    }

    override suspend fun put(
        url: String,
        body: String,
        parameters: Map<String, String>
    ): HttpClientResponse {
        val response = httpClient.post(url) {
            for ((key, value) in parameters) {
                parameter(key, value)
            }
            accept(VndApiJson)
            contentType(VndApiJson)
            setBody(body)
        }

        val responseBody = response.bodyAsText()
        return object : HttpClientResponse {
            override val statusCode: Int = response.status.value
            override val body: String = responseBody
        }
    }

    override suspend fun delete(url: String, parameters: Map<String, String>): HttpClientResponse {
        val response = httpClient.delete(url) {
            parameters.forEach { (key, value) ->
                parameter(key, value)
            }
            accept(VndApiJson)
        }

        val responseBody = response.bodyAsText()
        return object : HttpClientResponse {
            override val statusCode: Int = response.status.value
            override val body: String = responseBody
        }
    }
}