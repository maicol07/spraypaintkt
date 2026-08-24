package it.maicol07.spraypaintkt_test.models.openapi

import it.maicol07.spraypaintkt_annotation.Attr
import it.maicol07.spraypaintkt_annotation.ResourceSchema
import kotlin.String

@ResourceSchema(
    resourceType = "OpenApiPerson",
    endpoint = "openapi-people",
)
public interface OpenApiPersonSchema {
    @Attr
    public var name: String

    @Attr
    public var email: String?
}
