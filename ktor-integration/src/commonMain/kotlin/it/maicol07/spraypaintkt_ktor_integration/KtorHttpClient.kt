package it.maicol07.spraypaintkt_ktor_integration

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import it.maicol07.spraypaintkt.http.HttpResponse

class KtorHttpClient(
    httpClientOptions: HttpClientConfig<*>.() -> Unit = {},
    val httpClient: HttpClient = HttpClient(httpClientOptions)
): it.maicol07.spraypaintkt.http.HttpClient {
    override suspend fun get(url: String, parameters: Map<String, String>): HttpResponse {
        val response = httpClient.get(url) {
            for ((key, value) in parameters) {
                parameter(key, value)
            }
        }
        return HttpResponse(
            response.status.value,
            response.bodyAsText()
        )
    }

    override suspend fun post(
        url: String,
        body: String,
        parameters: Map<String, String>
    ): HttpResponse {
        val response = httpClient.post(url) {
            for ((key, value) in parameters) {
                parameter(key, value)
            }
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        return HttpResponse(
            response.status.value,
            response.bodyAsText()
        )
    }

    override suspend fun put(
        url: String,
        body: String,
        parameters: Map<String, String>
    ): HttpResponse {
        val response = httpClient.post(url) {
            for ((key, value) in parameters) {
                parameter(key, value)
            }
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        return HttpResponse(
            response.status.value,
            response.bodyAsText()
        )
    }

    override suspend fun delete(url: String, parameters: Map<String, String>): HttpResponse {
        val response = httpClient.delete(url) {
            parameters.forEach { (key, value) ->
                parameter(key, value)
            }
        }
        return HttpResponse(
            response.status.value,
            response.bodyAsText()
        )
    }
}