package it.maicol07.spraypaintkt.sample.data.models

import it.maicol07.spraypaintkt_annotation.Attr
import it.maicol07.spraypaintkt_annotation.Relation
import it.maicol07.spraypaintkt_annotation.ResourceSchema

@ResourceSchema(resourceType = "Book", endpoint = "Books")
abstract class BookSchema {
    @Attr abstract val title: String
    @Attr abstract val publisherId: Int

    @Relation open val reviews: MutableList<out ReviewSchema> = mutableListOf(Review())
    @Relation abstract val publisher: PublisherSchema
    @Relation abstract val author: PersonSchema
    @Relation abstract val reader: PersonSchema
}