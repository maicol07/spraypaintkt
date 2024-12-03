package it.maicol07.spraypaintkt_test.models

import it.maicol07.spraypaintkt_annotation.Attr
import it.maicol07.spraypaintkt_annotation.ResourceSchema
import it.maicol07.spraypaintkt_annotation.Relation

@ResourceSchema(resourceType = "Book", endpoint = "Books")
abstract class BookSchema {
    @Attr abstract val title: String
    @Attr abstract var publisherId: Int

    @Relation abstract val reviews: List<ReviewSchema>
    @Relation abstract val publisher: PublisherSchema
    @Relation abstract val author: PersonSchema
    @Relation abstract val reader: PersonSchema
}