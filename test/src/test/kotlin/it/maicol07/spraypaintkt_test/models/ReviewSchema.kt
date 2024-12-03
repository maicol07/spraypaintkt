package it.maicol07.spraypaintkt_test.models

import it.maicol07.spraypaintkt.Resource
import it.maicol07.spraypaintkt_annotation.Attr
import it.maicol07.spraypaintkt_annotation.Relation
import it.maicol07.spraypaintkt_annotation.ResourceSchema

@ResourceSchema("Review", "Reviews")
interface ReviewSchema {
    @Attr var review: String
    @Attr var created: String
    @Attr var book_id: String
    @Attr var reader_id: Int

    @Relation val book: BookSchema
    @Relation val reader: PersonSchema
}