package it.maicol07.spraypaintkt.tests.models

import it.maicol07.spraypaintkt.Resource

class Post: Resource("posts") {
    val title: String by attributes
    val content: String by attributes
    val createdAt: String by attributes
    val user: User? by relationships
    val discussion: Discussion? by relationships
    val tags: List<Tag> by relationships
}