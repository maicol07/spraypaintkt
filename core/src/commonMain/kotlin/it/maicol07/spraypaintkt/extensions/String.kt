package it.maicol07.spraypaintkt.extensions

private const val HEX_DIGITS = "0123456789ABCDEF"

/** Encodes a string as a UTF-8 URL path segment. */
internal fun String.encodePathSegment(): String = encodeToByteArray().joinToString("") { byte ->
    val value = byte.toInt() and 0xff
    val character = value.toChar()
    if (character in 'a'..'z' || character in 'A'..'Z' || character in '0'..'9' || character in "-._~") {
        character.toString()
    } else {
        "%${HEX_DIGITS[value shr 4]}${HEX_DIGITS[value and 0x0f]}"
    }
}
