package it.maicol07.spraypaintkt_test.models.openapi

import it.maicol07.spraypaintkt_annotation.Attr
import it.maicol07.spraypaintkt_annotation.Relation
import it.maicol07.spraypaintkt_annotation.ResourceSchema
import kotlin.Any
import kotlin.Boolean
import kotlin.Float
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map

@ResourceSchema(
    resourceType = "OpenApiBook",
    endpoint = "openapi-books",
)
public interface OpenApiBookSchema {
    @Attr
    public var title: String

    @Attr
    public var pageCount: Int

    @Attr
    public var publisherId: Int?

    @Attr
    public var rating: Float?

    @Attr
    public var published: Boolean?

    @Attr
    public var tags: List<String>?

    @Attr
    public var metadata: Map<String, Any?>?

    @Relation
    public val author: OpenApiPersonSchema

    @Relation
    public val reader: OpenApiPersonSchema?

    @Relation
    public val reviews: List<OpenApiReviewSchema>
}
