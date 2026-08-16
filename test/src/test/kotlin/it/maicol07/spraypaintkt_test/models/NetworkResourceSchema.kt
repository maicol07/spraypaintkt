package it.maicol07.spraypaintkt_test.models

import it.maicol07.spraypaintkt.PaginationStrategy
import it.maicol07.spraypaintkt.interfaces.HttpClient
import it.maicol07.spraypaintkt.interfaces.HttpClientResponse
import it.maicol07.spraypaintkt.interfaces.JsonApiConfig
import it.maicol07.spraypaintkt_annotation.Attr
import it.maicol07.spraypaintkt_annotation.ResourceSchema

internal data class StubResponse(
    override val statusCode: Int,
    override val body: String,
) : HttpClientResponse

internal object RecordingHttpClient : HttpClient {
    var response = StubResponse(200, "{\"data\":[]}")
    var lastUrl: String = ""
    var lastParameters: Map<String, String> = emptyMap()

    override suspend fun get(url: String, parameters: Map<String, String>): HttpClientResponse {
        lastUrl = url
        lastParameters = parameters
        return response
    }

    override suspend fun patch(url: String, body: String, parameters: Map<String, String>) = response
    override suspend fun post(url: String, body: String, parameters: Map<String, String>) = response
    override suspend fun put(url: String, body: String, parameters: Map<String, String>) = response
    override suspend fun delete(url: String, parameters: Map<String, String>) = response
}

data object RegressionConfig : JsonApiConfig {
    override val baseUrl = "https://example.invalid"
    override val paginationStrategy = PaginationStrategy.OFFSET_BASED
    override val httpClient: HttpClient = RecordingHttpClient
}

@ResourceSchema("network-resource", "network-resources", RegressionConfig::class)
interface NetworkResourceSchema {
    @Attr var name: String
}
