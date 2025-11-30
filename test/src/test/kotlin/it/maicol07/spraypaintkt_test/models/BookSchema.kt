package it.maicol07.spraypaintkt_test.models

import it.maicol07.spraypaintkt_annotation.Attr
import it.maicol07.spraypaintkt_annotation.ResourceSchema
import it.maicol07.spraypaintkt_annotation.Relation

enum class BookGenre {
    FICTION,
    NON_FICTION,
    SCIENCE_FICTION,
    FANTASY,
    MYSTERY,
    ROMANCE,
    THRILLER,
    BIOGRAPHY
}

@ResourceSchema(resourceType = "Book", endpoint = "Books")
abstract class BookSchema {
    @Attr abstract val title: String
    @Attr abstract var publisherId: Int
    @Attr abstract var genre: BookGenre?

    @Relation abstract val reviews: List<ReviewSchema>
    @Relation abstract val publisher: PublisherSchema
    @Relation abstract val author: PersonSchema
    @Relation abstract val reader: PersonSchema
}
