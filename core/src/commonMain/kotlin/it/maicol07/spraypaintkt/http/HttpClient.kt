package it.maicol07.spraypaintkt.http

interface HttpClient {
    suspend fun get(url: String, parameters: Map<String, String> = emptyMap()): HttpClientResponse
    suspend fun patch(url: String, body: String, parameters: Map<String, String> = emptyMap()): HttpClientResponse
    suspend fun post(url: String, body: String, parameters: Map<String, String> = emptyMap()): HttpClientResponse
    suspend fun put(url: String, body: String, parameters: Map<String, String> = emptyMap()): HttpClientResponse
    suspend fun delete(url: String, parameters: Map<String, String> = emptyMap()): HttpClientResponse
}