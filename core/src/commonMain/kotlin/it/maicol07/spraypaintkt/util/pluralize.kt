package it.maicol07.spraypaintkt.util

/**
 * @source https://github.com/cesarferreira/kotlin-pluralizer/blob/master/library/src/main/kotlin/com/cesarferreira/pluralize/Pluralize.kt
 */

import kotlin.math.abs

/**
 * Pluralize a string
 */
fun String.pluralize(count: Int = 2): String {
    return if (abs(count)!= 1)
        this.pluralizer()
    else
        this.singularizer()
}

/**
 * Singularize a string
 */
fun String.singularize(count: Int = 1): String = pluralize(count)

/**
 * Pluralize a string
 */
private fun String.pluralizer(): String {
    if (unCountable().contains(this.lowercase())) return this
    val rule = pluralizeRules().last { it.first.toRegex(RegexOption.IGNORE_CASE).containsMatchIn(this) }
    var found = rule.first.toRegex(RegexOption.IGNORE_CASE).replace(this, rule.second)
    val endsWith = exceptions().firstOrNull { this.endsWith(it.first) }
    if (endsWith != null) found = this.replace(endsWith.first, endsWith.second)
    val exception = exceptions().firstOrNull { this.equals(it.first, true) }
    if (exception != null) found = exception.second
    return found
}

/**
 * Singularize a string
 */
@Throws(RuntimeException::class)
private fun String.singularizer(): String {
    if (unCountable().contains(this.lowercase())) {
        return this
    }
    val exceptions = exceptions().firstOrNull { this.equals(it.second, true) }

    if (exceptions != null) {
        return exceptions.first
    }
    val endsWith = exceptions().firstOrNull { this.endsWith(it.second) }

    if (endsWith != null) return this.replace(endsWith.second, endsWith.first)

    try {
        if (singularizeRules().count { it.first.toRegex(RegexOption.IGNORE_CASE).containsMatchIn(this) } == 0) return this
        val rule = singularizeRules().last { it.first.toRegex(RegexOption.IGNORE_CASE).containsMatchIn(this) }
        return rule.first.toRegex(RegexOption.IGNORE_CASE).replace(this, rule.second)
    } catch(ex: IllegalArgumentException) {
        throw RuntimeException("Can't singularize this word, could not find a rule to match.")
    }
}

/**
 * List of uncountable words
 */
fun unCountable(): List<String> {
    return listOf("equipment", "information", "rice", "money",
        "species", "series", "fish", "sheep", "aircraft", "bison",
        "flounder", "pliers", "bream",
        "gallows", "proceedings", "breeches", "graffiti", "rabies",
        "britches", "headquarters", "salmon", "carp", "herpes",
        "scissors", "chassis", "high-jinks", "sea-bass", "clippers",
        "homework", "cod", "innings", "shears",
        "contretemps", "jackanapes", "corps", "mackerel",
        "swine", "debris", "measles", "trout", "diabetes", "mews",
        "tuna", "djinn", "mumps", "whiting", "eland", "news",
        "wildebeest", "elk", "pincers", "sugar")
}

/**
 * List of exceptions
 */
fun exceptions(): List<Pair<String, String>> {
    return listOf("person" to "people",
        "man" to "men",
        "goose" to "geese",
        "child" to "children",
        "sex" to "sexes",
        "move" to "moves",
        "stadium" to "stadiums",
        "deer" to "deer",
        "codex" to "codices",
        "murex" to "murices",
        "silex" to "silices",
        "radix" to "radices",
        "helix" to "helices",
        "alumna" to "alumnae",
        "alga" to "algae",
        "vertebra" to "vertebrae",
        "persona" to "personae",
        "stamen" to "stamina",
        "foramen" to "foramina",
        "lumen" to "lumina",
        "afreet" to "afreeti",
        "afrit" to "afriti",
        "efreet" to "efreeti",
        "cherub" to "cherubim",
        "goy" to "goyim",
        "human" to "humans",
        "lumen" to "lumina",
        "seraph" to "seraphim",
        "Alabaman" to "Alabamans",
        "Bahaman" to "Bahamans",
        "Burman" to "Burmans",
        "German" to "Germans",
        "Hiroshiman" to "Hiroshimans",
        "Liman" to "Limans",
        "Nakayaman" to "Nakayamans",
        "Oklahoman" to "Oklahomans",
        "Panaman" to "Panamans",
        "Selman" to "Selmans",
        "Sonaman" to "Sonamans",
        "Tacoman" to "Tacomans",
        "Yakiman" to "Yakimans",
        "Yokohaman" to "Yokohamans",
        "Yuman" to "Yumans", "criterion" to "criteria",
        "perihelion" to "perihelia",
        "aphelion" to "aphelia",
        "phenomenon" to "phenomena",
        "prolegomenon" to "prolegomena",
        "noumenon" to "noumena",
        "organon" to "organa",
        "asyndeton" to "asyndeta",
        "hyperbaton" to "hyperbata",
        "foot" to "feet")
}

/**
 * List of pluralization rules
 */
fun pluralizeRules(): List<Pair<String, String>> {
    return listOf(
        "(.)$" to "$1s",
        "s$" to "s",
        "(ax|test)is$" to "$1es",
        "us$" to "i",
        "(octop|vir)us$" to "$1i",
        "(octop|vir)i$" to "$1i",
        "(alias|status)$" to "$1es",
        "(bu)s$" to "$1ses",
        "(buffal|tomat)o$" to "$1oes",
        "([ti])um$" to "$1a",
        "([ti])a$" to "$1a",
        "sis$" to "ses",
        "(,:([^f])fe|([lr])f)$" to "$1$2ves",
        "(hive)$" to "$1s",
        "([^aeiouy]|qu)y$" to "$1ies",
        "(x|ch|ss|sh)$" to "$1es",
        "(matr|vert|ind)ix|ex$" to "$1ices",
        "([m|l])ouse$" to "$1ice",
        "([m|l])ice$" to "$1ice",
        "^(ox)$" to "$1en",
        "(quiz)$" to "$1zes",
        "f$" to "ves",
        "fe$" to "ves",
        "um$" to "a",
        "on$" to "a",
        "tion" to "tions",
        "sion" to "sions")
}

/**
 * List of singularization rules
 */
fun singularizeRules(): List<Pair<String, String>> {
    return listOf(
        "s$" to "",
        "(s|si|u)s$" to "$1s",
        "(n)ews$" to "$1ews",
        "([ti])a$" to "$1um",
        "((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$" to "$1$2sis",
        "(^analy)ses$" to "$1sis",
        "(^analy)sis$" to "$1sis",
        "([^f])ves$" to "$1fe",
        "(hive)s$" to "$1",
        "(tive)s$" to "$1",
        "([lr])ves$" to "$1f",
        "([^aeiouy]|qu)ies$" to "$1y",
        "(s)eries$" to "$1eries",
        "(m)ovies$" to "$1ovie",
        "(x|ch|ss|sh)es$" to "$1",
        "([m|l])ice$" to "$1ouse",
        "(bus)es$" to "$1",
        "(o)es$" to "$1",
        "(shoe)s$" to "$1",
        "(cris|ax|test)is$" to "$1is",
        "(cris|ax|test)es$" to "$1is",
        "(octop|vir)i$" to "$1us",
        "(octop|vir)us$" to "$1us",
        "(alias|status)es$" to "$1",
        "(alias|status)$" to "$1",
        "^(ox)en" to "$1",
        "(vert|ind)ices$" to "$1ex",
        "(matr)ices$" to "$1ix",
        "(quiz)zes$" to "$1",
        "a$" to "um",
        "i$" to "us",
        "ae$" to "a")
}