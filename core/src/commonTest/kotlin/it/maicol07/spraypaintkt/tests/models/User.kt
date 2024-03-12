package it.maicol07.spraypaintkt.tests.models

import it.maicol07.spraypaintkt.Resource

class User: Resource() {
    val username: String by attributes
    val displayName: String by attributes
    val bio: String by attributes
    val slug: String by attributes
}