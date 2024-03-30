package it.maicol07.spraypaintkt

import it.maicol07.spraypaintkt.extensions.JsonObjectMap
import it.maicol07.spraypaintkt.extensions.extractedContent
import it.maicol07.spraypaintkt.util.Deserializer
import it.maicol07.spraypaintkt.http.HttpClient
import kotlinx.serialization.json.Json

/**
 * A client for a JSON:API server.
 *
 * @param baseUrl The base URL of the server.
 * @param apiNamespace The namespace of the API.
 * @param paginationStrategy The pagination strategy to use.
 * @param httpClient The HTTP client to use.
 * @param modelGenerator The model generator to use.
 */
@Suppress("unused")
class Client(
    baseUrl: String,
    apiNamespace: String = "",
    val paginationStrategy: PaginationStrategy = PaginationStrategy.PAGE_BASED,
    val httpClient: HttpClient,
    val modelGenerator: ModelGenerator
) {
    /**
     * The type registry for the client.
     */
    val typeRegistry = mutableMapOf<String, () -> Resource>()
    /**
     * The deserializer for the client.
     */
    val deserializer = Deserializer(typeRegistry)
    /**
     * The base URL of the server.
     */
    private val fullBasePath = "$baseUrl$apiNamespace"

    /**
     * Create a new scope.
     */
    fun scope(): Scope {
        return Scope(this)
    }

    /**
     * Set the number of items per page (Only for page-based pagination)
     *
     * @param pageSize The number of items per page.
     */
    fun per(pageSize: Number): Scope {
        return scope().per(pageSize)
    }


    /**
     * Set the page number (Only for page-based pagination)
     *
     * @param pageNumber The page number.
     */
    fun page(pageNumber: Number): Scope {
        return scope().page(pageNumber)
    }

    /**
     * Set the number of items to limit (Only for offset-based pagination)
     *
     * @param limit The number of items to limit.
     */
    fun limit(limit: Number): Scope {
        return scope().limit(limit)
    }

    /**
     * Set the number of items to skip (Only for offset-based pagination)
     *
     * @param offset The number of items to skip.
     */
    fun offset(offset: Number): Scope {
        return scope().offset(offset)
    }

    /**
     * Set the filter for the scope.
     *
     * @param key The key of the filter.
     * @param value The value of the filter.
     */
    fun where(key: String, value: String): Scope {
        return scope().where(key, value)
    }

    /**
     * Set the order for the scope.
     *
     * @param key The key to order by.
     * @param direction The direction to order in.
     */
    fun order(key: String, direction: SortDirection): Scope {
        return scope().order(key, direction)
    }

    /**
     * Set the includes for the scope.
     *
     * @param relationships The relationships to include.
     */
    fun includes(vararg relationships: String): Scope {
        return scope().includes(*relationships)
    }

    /**
     * Set the fields for the scope.
     *
     * @param type The type to select fields for.
     * @param fields The fields to select.
     */
    fun select(type: String, vararg fields: String): Scope {
        return scope().select(type, *fields)
    }

    /**
     * Get all resources of a type.
     */
    suspend inline fun <reified R: Resource> all(): CollectionProxy<R> {
        return scope().all()
    }

    /**
     * Find a resource by its ID.
     *
     * @param id The ID of the resource.
     */
    suspend inline fun <reified R: Resource> find(id: String): RecordProxy<R> {
        return scope().find<R>(id)
    }

    /**
     * Get the first resource of a type.
     */
    suspend inline fun <reified R: Resource> first(): RecordProxy<R> {
        return scope().first<R>()
    }

    /**
     * Save a resource to the server.
     *
     * @param resource The resource to save.
     */
    suspend fun <R: Resource> save(resource: R): Boolean {
        val response = if (resource.isPersisted) {
            val url = urlForResource(resource, resource.id)
            httpClient.patch(url, resource.toJsonApiString())
        } else {
            val url = urlForResource(resource)
            httpClient.post(url, resource.toJsonApiString())
        }
        if (response.statusCode !in 200..204) {
            throw JsonApiException(response.statusCode, response.body)
        }
        
        if (!resource.isPersisted && response.statusCode == 201) {
            val jsonApiResponse = Json.parseToJsonElement(response.body).extractedContent as JsonObjectMap?
                ?: throw JsonApiException(response.statusCode, response.body)
            resource.fromJsonApi(JsonApiResource(jsonApiResponse["data"] as JsonObjectMap), emptyList(), deserializer)
            
        }
        
        return true
    }

    /**
     * Destroy a resource from the server.
     *
     * @param resource The resource to destroy.
     */
    suspend fun <R: Resource> destroy(resource: R): Boolean {
        val url = urlForResource(resource, resource.id)
        val response = httpClient.delete(url)
        if (response.statusCode !in listOf(200, 204)) {
            throw JsonApiException(response.statusCode, response.body)
        }
        return true
    }

    /**
     * Register a resource type.
     *
     * @param type The type of the resource.
     */
    inline fun <reified R: Resource> registerResource(type: String? = null): Client {
        val resource = modelGenerator.generate(R::class)
        typeRegistry[type ?: resource.type] = { modelGenerator.generate(R::class) }
        return this
    }

    /**
     * Get the URL for a resource.
     *
     * @param resource The resource to get the URL for.
     * @param id The ID of the resource.
     */
    fun <R: Resource> urlForResource(resource: R, id: String? = null): String {
        val endpoint = resource.endpoint ?: resource.type
        val effectiveId = id ?: resource.id
        return "$fullBasePath/$endpoint${effectiveId?.let { "/$it" } ?: ""}"
    }
}