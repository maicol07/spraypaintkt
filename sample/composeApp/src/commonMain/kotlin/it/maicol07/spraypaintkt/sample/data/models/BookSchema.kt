package it.maicol07.spraypaintkt_test.models

import it.maicol07.spraypaintkt_annotation.Attr
import it.maicol07.spraypaintkt_annotation.ResourceSchema
import it.maicol07.spraypaintkt_annotation.ToManyRelationship
import it.maicol07.spraypaintkt_annotation.ToOneRelationship

@ResourceSchema(resourceType = "Book", endpoint = "Books")
@ToManyRelationship("reviews", ReviewSchema::class)
@ToOneRelationship("publisher", PublisherSchema::class)
@ToOneRelationship("author", PersonSchema::class)
@ToOneRelationship("reader", PersonSchema::class)
abstract class BookSchema {
    @Attr abstract val title: String
    @Attr abstract var publisher_id: Int
}