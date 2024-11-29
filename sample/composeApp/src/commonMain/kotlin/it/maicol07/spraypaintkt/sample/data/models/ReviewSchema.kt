package it.maicol07.spraypaintkt_test.models

import it.maicol07.spraypaintkt_annotation.Attr
import it.maicol07.spraypaintkt_annotation.ToOneRelationship
import it.maicol07.spraypaintkt_annotation.ResourceSchema

@ResourceSchema("Review", "Reviews")
@ToOneRelationship("book", BookSchema::class)
@ToOneRelationship("reader", PersonSchema::class)
interface ReviewSchema {
    @Attr var review: String
    @Attr var created: String
    @Attr var book_id: String
    @Attr var reader_id: Int
}