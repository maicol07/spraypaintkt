package it.maicol07.spraypaintkt_test.models

import it.maicol07.spraypaintkt.Resource
import it.maicol07.spraypaintkt_annotation.Attr
import it.maicol07.spraypaintkt_annotation.ResourceSchema
import it.maicol07.spraypaintkt_annotation.Relation

@ResourceSchema("Publisher", "Publishers")
interface PublisherSchema: Resource {
    @Attr val name: String
    @Attr val stock: Int
    @Attr val customField: String

    @Relation val books: MutableList<out BookSchema>
}