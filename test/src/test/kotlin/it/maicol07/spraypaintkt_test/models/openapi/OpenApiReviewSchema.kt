package it.maicol07.spraypaintkt_test.models.openapi

import it.maicol07.spraypaintkt_annotation.Attr
import it.maicol07.spraypaintkt_annotation.ResourceSchema
import kotlin.String

@ResourceSchema(
    resourceType = "OpenApiReview",
    endpoint = "OpenApiReview",
)
public interface OpenApiReviewSchema {
    @Attr
    public var body: String?
}
