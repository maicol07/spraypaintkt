package it.maicol07.spraypaintkt_test.models

import it.maicol07.spraypaintkt_annotation.Relation
import it.maicol07.spraypaintkt_annotation.ResourceSchema

@ResourceSchema("relation-options", "relation-options")
interface RelationOptionsSchema {
    @Relation(name = "published_books", mutable = false, autoTransform = false)
    val books: List<BookSchema>
}
