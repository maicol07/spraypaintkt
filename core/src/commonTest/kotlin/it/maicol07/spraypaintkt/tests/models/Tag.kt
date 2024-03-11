package it.maicol07.spraypaintkt.tests.models

import it.maicol07.spraypaintkt.Resource

class Tag: Resource("tags") {
    val name: String by attributes
    val description: String by attributes
    val slug: String by attributes
    val color: String by attributes
    val icon: String by attributes

    val parent: Tag? by relationships
}