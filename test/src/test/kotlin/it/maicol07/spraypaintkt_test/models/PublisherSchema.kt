package it.maicol07.spraypaintkt_test.models

import it.maicol07.spraypaintkt_annotation.Attr
import it.maicol07.spraypaintkt_annotation.ResourceSchema
import it.maicol07.spraypaintkt_annotation.ToManyRelationship

@ResourceSchema("Publisher", "Publishers")
@ToManyRelationship("books", BookSchema::class)
interface PublisherSchema {
    @Attr val name: String
    @Attr val stock: Int
    @Attr val custom_field: String
}