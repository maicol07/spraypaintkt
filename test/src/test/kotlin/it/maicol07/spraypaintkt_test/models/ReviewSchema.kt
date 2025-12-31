package it.maicol07.spraypaintkt_test.models

import it.maicol07.spraypaintkt_annotation.Attr
import it.maicol07.spraypaintkt_annotation.Relation
import it.maicol07.spraypaintkt_annotation.ResourceSchema

@ResourceSchema("Review", "Reviews")
interface ReviewSchema {
    @Attr var review: String
    @Attr var created: String
    @Attr var bookId: String
    @Attr var readerId: Int

    @Relation val book: BookSchema
    @Relation val reader: PersonSchema
}
