package it.maicol07.spraypaintkt.tests.models

import it.maicol07.spraypaintkt.Resource

class Person: Resource("Person", "People") {
    var name: String by attributes
    var email: String by attributes
    var comment: String by attributes
    var dob: String by attributes
}