package it.maicol07.spraypaintkt_test.models

import it.maicol07.spraypaintkt_annotation.Attr
import it.maicol07.spraypaintkt_annotation.ResourceSchema
import it.maicol07.spraypaintkt_annotation.Relation

interface BaseBookSchema {
    @Attr val title: String
    @Attr var publisher_id: Int

    @Relation val reviews: MutableList<out ReviewSchema>
}

@ResourceSchema(resourceType = "Book", endpoint = "Books")
interface ExtendedBookSchema: BaseBookSchema {
    @Relation val publisher: PublisherSchema
    @Relation val author: PersonSchema
    @Relation val reader: PersonSchema
}