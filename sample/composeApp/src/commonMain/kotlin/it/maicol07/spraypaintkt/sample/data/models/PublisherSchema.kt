package it.maicol07.spraypaintkt.sample.data.models

import it.maicol07.spraypaintkt_annotation.Attr
import it.maicol07.spraypaintkt_annotation.Relation
import it.maicol07.spraypaintkt_annotation.ResourceSchema

@ResourceSchema("Publisher", "Publishers")
interface PublisherSchema {
    @Attr val name: String
    @Attr val stock: Int
    @Attr val customField: String

    @Relation val books: MutableList<out BookSchema>
}