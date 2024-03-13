package it.maicol07.spraypaintkt.http

interface HttpClientResponse {
    val statusCode: Int
    val body: String
}