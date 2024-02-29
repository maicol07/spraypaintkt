package it.maicol07.spraypaintkt.http

data class HttpResponse(
    val statusCode: Int,
    val body: String
)