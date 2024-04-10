package it.maicol07.spraypaintkt.tests.models

import it.maicol07.spraypaintkt.Resource

class Book: Resource("Book", "Books") {
    var title: String by attributes

    var author: Person by relationships
    var reader: Person by relationships
    var reviews: List<Review> by relationships
    var publisher: Publisher by relationships
}