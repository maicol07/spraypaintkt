package it.maicol07.spraypaintkt.http

interface HttpClient {
    suspend fun get(url: String, parameters: Map<String, String>): HttpResponse
    suspend fun post(url: String, body: String, parameters: Map<String, String>): HttpResponse
    suspend fun put(url: String, body: String, parameters: Map<String, String>): HttpResponse
    suspend fun delete(url: String, parameters: Map<String, String>): HttpResponse
}