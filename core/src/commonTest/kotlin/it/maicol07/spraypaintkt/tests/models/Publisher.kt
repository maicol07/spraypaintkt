package it.maicol07.spraypaintkt.tests.models

import it.maicol07.spraypaintkt.Resource

class Publisher: Resource("Publisher", "Publishers") {
    val name: String by attributes
    val stock: Int by attributes
    val custom_field: String by attributes

    val books: List<Book> by relationships
}