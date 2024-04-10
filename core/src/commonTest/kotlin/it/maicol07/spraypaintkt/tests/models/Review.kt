package it.maicol07.spraypaintkt.tests.models

import it.maicol07.spraypaintkt.Resource

class Review: Resource("Review", "Reviews") {
    val review: String by attributes
    val created: String by attributes

    val book: Book by relationships
    val reader: Person by relationships
}