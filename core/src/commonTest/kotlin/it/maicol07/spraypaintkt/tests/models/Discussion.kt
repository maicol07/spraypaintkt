package it.maicol07.spraypaintkt.tests.models

import it.maicol07.spraypaintkt.Resource

class Discussion: Resource() {
    val title: String by attributes
    val slug: String by attributes
    val commentCount: Int? by attributes
    val participantCount: Int? by attributes
    val createdAt: String by attributes
    val lastPostedAt: String by attributes
    val lastPostedUser: User? by relationships
    val user: User? by relationships
    val tags: List<Tag> by relationships
}