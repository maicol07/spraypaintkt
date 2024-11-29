package it.maicol07.spraypaintkt_test.models

import it.maicol07.spraypaintkt_annotation.Attr
import it.maicol07.spraypaintkt_annotation.ResourceSchema

@ResourceSchema("Person", "People")
interface PersonSchema {
    @Attr var name: String
    @Attr var email: String
    @Attr var comment: String
    @Attr var dob: String
}