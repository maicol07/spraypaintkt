package it.maicol07.spraypaintkt

import it.maicol07.spraypaintkt.extensions.JsonObjectMap
import it.maicol07.spraypaintkt.extensions.extractedContent
import it.maicol07.spraypaintkt.util.Deserializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

/**
 * A scope for querying the server.
 *
 * @param resourceClass The resource to use the scope on.
 * @param options The options for the scope.
 */
class Scope<R: Resource>(private val resourceClass: KClass<R>, options: Scope<R>.() -> Unit = {}) {
    annotation class ScopeMethod

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

    private val resourceCompanion = ResourceRegistry.get(resourceClass)

    init {
        options()
    }

    /**
     * Get all the resources.
     */
    @ScopeMethod
    suspend fun all(): CollectionProxy<R> {
        val json = this.sendRequest(resourceCompanion.urlForResource())
        return buildResultList(JsonApiCollectionResponse(json))
    }

    /**
     * Find a resource by its ID
     *
     * @param id The ID of the record
     */
    @ScopeMethod
    suspend fun find(id: String): RecordProxy<R> {
        val json = this.sendRequest(resourceCompanion.urlForResource(id = id))
        return buildRecordResult(JsonApiSingleResponse(json))
    }

    /**
     * Get the first resource
     */
    @ScopeMethod
    suspend fun first(): RecordProxy<R> {
        val json = this.sendRequest(resourceCompanion.urlForResource())
        return buildRecordResult(JsonApiSingleResponse(json))
    }

    /**
     * Set the page number to return (Only for page-based pagination)
     *
     * @param pageNumber The page number to return
     */
    @ScopeMethod
    fun page(pageNumber: Number): Scope<R> {
        if (resourceCompanion.config.paginationStrategy == PaginationStrategy.OFFSET_BASED) {
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
    @ScopeMethod
    fun per(pageSize: Number): Scope<R> {
        if (resourceCompanion.config.paginationStrategy == PaginationStrategy.OFFSET_BASED) {
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
    @ScopeMethod
    fun limit(limit: Number): Scope<R> {
        if (resourceCompanion.config.paginationStrategy == PaginationStrategy.PAGE_BASED) {
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
    @ScopeMethod
    fun offset(offset: Number): Scope<R> {
        if (resourceCompanion.config.paginationStrategy == PaginationStrategy.PAGE_BASED) {
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
    @ScopeMethod
    fun where(attribute: String, value: Any): Scope<R> {
        filter[attribute] = value.toString()
        return this
    }

    /**
     * Add an extra parameter to the request
     *
     * @param key The key of the parameter
     * @param value The value of the parameter
     */
    @ScopeMethod
    fun extraParam(key: String, value: String): Scope<R> {
        params[key] = value
        return this
    }

    /**
     * Order the resources by an attribute
     *
     * @param attribute The attribute to order by
     * @param sortDirection The direction to order in
     */
    @ScopeMethod
    fun order(attribute: String, sortDirection: SortDirection = SortDirection.ASC): Scope<R> {
        sort[attribute] = sortDirection
        return this
    }

    /**
     * Include the relationships in the response
     *
     * @param relationships The relationships to include
     */
    @ScopeMethod
    fun includes(vararg relationships: String): Scope<R> {
        includes.addAll(relationships)
        return this
    }

    /**
     * Select the fields to return
     *
     * @param type The type to select fields for
     * @param fields The fields to select
     */
    @ScopeMethod
    fun select(type: String, vararg fields: String): Scope<R> {
        this.fields[type] = fields.toList()
        return this
    }

    /**
     * Send a request to the server
     *
     * @param url The URL to send the request to
     */
    private suspend fun sendRequest(url: String): JsonObjectMap {
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

        val response = resourceCompanion.config.httpClient.get(url, params)
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
     */
    private fun buildRecordResult(jsonResult: JsonApiSingleResponse): RecordProxy<R> {
        val model = ResourceRegistry.createInstance(resourceClass)
        val data = jsonResult.data ?: throw RuntimeException("Record not found")
        model.fromJsonApi(data, jsonResult.included)
        return RecordProxy(model, jsonResult.meta, jsonResult)
    }

    /**
     * Build a result list from a JSON:API response
     *
     * @param jsonResult The JSON:API response
     */
    private fun buildResultList(jsonResult: JsonApiCollectionResponse): CollectionProxy<R> {
        val modelList = mutableListOf<R>()

        for (record in jsonResult.data) {
            val model = ResourceRegistry.createInstance(resourceClass)
            model.fromJsonApi(record, jsonResult.included)
            modelList.add(model)
        }

        return CollectionProxy(modelList, jsonResult.meta, jsonResult)
    }
}