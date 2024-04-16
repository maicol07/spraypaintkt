package it.maicol07.spraypaintkt

import it.maicol07.spraypaintkt.extensions.JsonObjectMap
import it.maicol07.spraypaintkt.extensions.extractedContent
import it.maicol07.spraypaintkt.util.Deserializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/**
 * The direction of the sorting.
 */
enum class SortDirection {
    ASC,
    DESC
}

/**
 * The strategy to use for pagination.
 */
enum class PaginationStrategy {
    PAGE_BASED,
    OFFSET_BASED
}

/**
 * The pagination options.
 *
 * @param number The page number.
 * @param size The number of items per page.
 * @param limit The number of items to return.
 * @param offset The number of items to skip.
 */
data class ScopePagination(
    var number: Number? = null,
    var size: Number? = null,
    var limit: Number? = null,
    var offset: Number? = null
)

/**
 * A wrapper for a collection of resources.
 *
 * @param R The type of the resource.
 * @param data The data of the collection.
 * @param meta The meta of the collection.
 * @param raw The raw JSON:API response as a map.
 */
data class CollectionProxy<R: Resource>(
    val data: List<R>,
    val meta: Map<String, Any>,
    val raw: JsonApiCollectionResponse
)

/**
 * A wrapper for a single resource.
 *
 * @param R The type of the resource.
 * @param data The data of the resource.
 * @param meta The meta of the resource.
 * @param raw The raw JSON:API response as a map.
 */
data class RecordProxy<R: Resource>(
    val data: R,
    val meta: Map<String, Any>,
    val raw: JsonApiSingleResponse
)

/**
 * A scope for querying the server.
 *
 * @param client The client to use.
 * @param options The options for the scope.
 */
class Scope(val client: Client, options: Scope.() -> Unit = {}) {
    /** The pagination options. */
    val pagination = ScopePagination()
    /** The filter options. */
    val filter = mutableMapOf<String, String>()
    /** The sort options. */
    val sort = mutableMapOf<String, SortDirection>()
    /** The extra parameters. */
    val params = mutableMapOf<String, String>()
    /** The fields to select. */
    val fields = mutableMapOf<String, List<String>>()
    /** The relationships to include. */
    val includes = mutableListOf<String>()

    init {
        options()
    }

    /**
     * Get all the resources.
     *
     * @param R The type of the resource.
     */
    suspend inline fun <reified R: Resource> all(): CollectionProxy<R> {
        val model = client.modelGenerator.generate(R::class)
        val json = this.sendRequest(client.urlForResource(model))
        return buildResultList(JsonApiCollectionResponse(json))
    }

    /**
     * Find a resource by its ID
     *
     * @param id The ID of the record
     * @param R The type of the resource
     */
    suspend inline fun <reified R: Resource> find(id: String): RecordProxy<R> {
        val model = client.modelGenerator.generate(R::class)
        val json = this.sendRequest(client.urlForResource(model, id))
        return buildRecordResult(JsonApiSingleResponse(json))
    }

    /**
     * Get the first resource
     *
     * @param R The type of the resource
     */
    suspend inline fun <reified R: Resource> first(): RecordProxy<R> {
        val model = client.modelGenerator.generate(R::class)
        val json = this.sendRequest(client.urlForResource(model))
        return buildRecordResult(JsonApiSingleResponse(json))
    }

    /**
     * Set the page number to return (Only for page-based pagination)
     *
     * @param pageNumber The page number to return
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
     *
     * @param pageSize The number of items per page
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
     *
     * @param limit The number of items to return
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
     *
     * @param offset The number of items to skip
     */
    fun offset(offset: Number): Scope {
        if (client.paginationStrategy == PaginationStrategy.PAGE_BASED) {
            throw RuntimeException("Offset-based pagination is not supported with the current pagination strategy")
        }
        pagination.offset = offset
        return this
    }

    /**
     * Filter the resources by an attribute
     *
     * @param attribute The attribute to filter by
     * @param value The value to filter by
     */
    fun where(attribute: String, value: Any): Scope {
        filter[attribute] = value.toString()
        return this
    }

    /**
     * Add an extra parameter to the request
     *
     * @param key The key of the parameter
     * @param value The value of the parameter
     */
    fun extraParam(key: String, value: String): Scope {
        params[key] = value
        return this
    }

    /**
     * Order the resources by an attribute
     *
     * @param attribute The attribute to order by
     * @param sortDirection The direction to order in
     */
    fun order(attribute: String, sortDirection: SortDirection = SortDirection.ASC): Scope {
        sort[attribute] = sortDirection
        return this
    }

    /**
     * Include the relationships in the response
     *
     * @param relationships The relationships to include
     */
    fun includes(vararg relationships: String): Scope {
        includes.addAll(relationships)
        return this
    }

    /**
     * Select the fields to return
     *
     * @param type The type to select fields for
     * @param fields The fields to select
     */
    fun select(type: String, vararg fields: String): Scope {
        this.fields[type] = fields.toList()
        return this
    }

    /**
     * Send a request to the server
     *
     * @param url The URL to send the request to
     */
    suspend fun sendRequest(url: String): JsonObjectMap {
        val params = mutableMapOf(
            *filter.map { (key, value) -> "filter[$key]" to value }.toTypedArray(),
            *sort.map { (key, value) -> "sort" to (if (value == SortDirection.ASC) key else "-$key") }.toTypedArray(),
            *params.toList().toTypedArray(),
            *fields.map { (key, value) -> "fields[$key]" to value.joinToString(",") }.toTypedArray()
        )

        if (includes.isNotEmpty()) {
            params["include"] = includes.joinToString(",")
        }

        pagination.number?.let { params.put("page[number]", it.toString()) }
        pagination.size?.let { params.put("page[size]", it.toString()) }
        pagination.limit?.let { params.put("page[limit]", it.toString()) }
        pagination.offset?.let { params.put("page[offset]", it.toString()) }

        val response = client.httpClient.get(url, params)
        if (response.statusCode >= 400) {
            throw JsonApiException(response.statusCode, response.body)
        }

        try {
            @Suppress("UNCHECKED_CAST")
            return Json.parseToJsonElement(response.body).extractedContent as JsonObjectMap?
                ?: throw RuntimeException("Empty response from JSONAPI")
        } catch (e: SerializationException) {
            throw RuntimeException("Failed to decode JSONAPI Response")
        }
    }

    /**
     * Build a record result from a JSON:API response
     *
     * @param jsonResult The JSON:API response
     * @param R The type of the resource
     */
    inline fun <reified R: Resource> buildRecordResult(jsonResult: JsonApiSingleResponse): RecordProxy<R> {
        val model = client.modelGenerator.generate(R::class)
        val data = jsonResult.data ?: throw RuntimeException("Record not found")
        model.fromJsonApi(data, jsonResult.included, Deserializer(client.typeRegistry))
        return RecordProxy(model, jsonResult.meta, jsonResult)
    }

    /**
     * Build a result list from a JSON:API response
     *
     * @param jsonResult The JSON:API response
     * @param R The type of the resource
     */
    inline fun <reified R: Resource> buildResultList(jsonResult: JsonApiCollectionResponse): CollectionProxy<R> {
        val modelList = mutableListOf<R>()

        for (record in jsonResult.data) {
            val model = client.modelGenerator.generate(R::class)
            model.fromJsonApi(record, jsonResult.included, Deserializer(client.typeRegistry))
            modelList.add(model)
        }

        return CollectionProxy(modelList, jsonResult.meta, jsonResult)
    }
}