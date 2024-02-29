package it.maicol07.spraypaintkt

import it.maicol07.spraypaintkt.extensions.JsonObjectMap
import it.maicol07.spraypaintkt.extensions.extractedContent
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

enum class SortDirection {
    ASC,
    DESC
}

enum class PaginationStrategy {
    PAGE_BASED,
    OFFSET_BASED
}

data class ScopePagination(
    var number: Number? = null,
    var size: Number? = null,
    var limit: Number? = null,
    var offset: Number? = null
)

data class CollectionProxy<R: Resource>(
    val data: List<R>,
    val meta: Map<String, Any>,
    val raw: JsonApiCollectionResponse
)

data class RecordProxy<R: Resource>(
    val data: R,
    val meta: Map<String, Any>,
    val raw: JsonApiSingleResponse
)

class Scope(val client: Client, options: Scope.() -> Unit = {}) {
    val pagination = ScopePagination()
    val filter = mutableMapOf<String, String>()
    val sort = mutableMapOf<String, SortDirection>()
    val params = mutableMapOf<String, String>()
    val fields = mutableMapOf<String, List<String>>()
    val includes = mutableListOf<String>()

    init {
        options()
    }

    suspend inline fun <reified R: Resource> all(): CollectionProxy<R> {
        val model = client.modelGenerator.generate(R::class)
        val json = this.sendRequest(client.urlForResource(model))
        return buildResultList(JsonApiCollectionResponse(json))
    }

    suspend inline fun <reified R: Resource> find(id: String): RecordProxy<R> {
        val model = client.modelGenerator.generate(R::class)
        val json = this.sendRequest(client.urlForResource(model, id))
        return buildRecordResult(JsonApiSingleResponse(json))
    }

    suspend inline fun <reified R: Resource> first(): RecordProxy<R> {
        val model = client.modelGenerator.generate(R::class)
        val json = this.sendRequest(client.urlForResource(model))
        return buildRecordResult(JsonApiSingleResponse(json))
    }

    /**
     * Set the page number to return (Only for page-based pagination)
     */
    fun page(pageNumber: Number): Scope {
        if (client.paginationStrategy == PaginationStrategy.OFFSET_BASED) {
            throw RuntimeException("Page-based pagination is not supported with the current pagination strategy")
        }
        pagination.number = pageNumber
        return this
    }

    /**
     * Set the number of items per page (Only for page-based pagination)
     */
    fun per(pageSize: Number): Scope {
        if (client.paginationStrategy == PaginationStrategy.OFFSET_BASED) {
            throw RuntimeException("Page-based pagination is not supported with the current pagination strategy")
        }
        pagination.size = pageSize
        return this
    }

    /**
     * Set the number of items to return (Only for offset-based pagination)
     */
    fun limit(limit: Number): Scope {
        if (client.paginationStrategy == PaginationStrategy.PAGE_BASED) {
            throw RuntimeException("Offset-based pagination is not supported with the current pagination strategy")
        }
        pagination.limit = limit
        return this
    }

    /**
     * Set the number of items to skip (Only for offset-based pagination)
     */
    fun offset(offset: Number): Scope {
        if (client.paginationStrategy == PaginationStrategy.PAGE_BASED) {
            throw RuntimeException("Offset-based pagination is not supported with the current pagination strategy")
        }
        pagination.offset = offset
        return this
    }

    fun where(attribute: String, value: Any): Scope {
        filter[attribute] = value.toString()
        return this
    }

    fun extraParam(key: String, value: String): Scope {
        params[key] = value
        return this
    }

    fun order(attribute: String, sortDirection: SortDirection = SortDirection.ASC): Scope {
        sort[attribute] = sortDirection
        return this
    }

    fun includes(vararg relationships: String): Scope {
        includes.addAll(relationships)
        return this
    }

    fun select(type: String, vararg fields: String): Scope {
        this.fields[type] = fields.toList()
        return this
    }

    suspend fun sendRequest(url: String): JsonObjectMap {
        val params = mutableMapOf(
            *filter.map { (key, value) -> "filter[$key]" to value }.toTypedArray(),
            *sort.map { (key, value) -> "sort" to (if (value == SortDirection.ASC) key else "-$key") }.toTypedArray(),
            *params.toList().toTypedArray(),
            *fields.map { (key, value) -> "fields[$key]" to value.joinToString(",") }.toTypedArray()
        )

        if (includes.isNotEmpty()) {
            params.put("include", includes.joinToString(","))
        }

        pagination.number?.let { params.put("page[number]", it.toString()) }
        pagination.size?.let { params.put("page[size]", it.toString()) }
        pagination.limit?.let { params.put("page[limit]", it.toString()) }
        pagination.offset?.let { params.put("page[offset]", it.toString()) }

        val response = client.httpClient.get(url, params)
        if (response.statusCode != 200) {
            throw RuntimeException("JSONAPI Request failed with status ${response.statusCode}")
        }

        try {
            @Suppress("UNCHECKED_CAST")
            return Json.parseToJsonElement(response.body).extractedContent as JsonObjectMap?
                ?: throw RuntimeException("Empty response from JSONAPI")
        } catch (e: SerializationException) {
            throw RuntimeException("Failed to decode JSONAPI Response")
        }
    }

    inline fun <reified R: Resource> buildRecordResult(jsonResult: JsonApiSingleResponse): RecordProxy<R> {
        val model = client.modelGenerator.generate(R::class)
        val data = jsonResult.data ?: throw RuntimeException("Record not found")
        model.fromJsonApi(data, jsonResult.included, client.deserializer)
        return RecordProxy(model, jsonResult.meta, jsonResult)
    }

    inline fun <reified R: Resource> buildResultList(jsonResult: JsonApiCollectionResponse): CollectionProxy<R> {
        val modelList = mutableListOf<R>()

        for (record in jsonResult.data) {
            val model = client.modelGenerator.generate(R::class)
            model.fromJsonApi(record, jsonResult.included, client.deserializer)
            modelList.add(model)
        }

        return CollectionProxy(modelList, jsonResult.meta, jsonResult)
    }
}