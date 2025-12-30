package it.maicol07.spraypaintkt

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
data class RecordProxy<R>(
    val data: R,
    val meta: Map<String, Any>,
    val raw: JsonApiSingleResponse
)
