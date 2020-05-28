package slatekit.common.ext

import org.threeten.bp.LocalTime
import slatekit.common.Strings
import java.util.*

/**
 * returns a new string that truncates the string supplied by the count of chars.
 */
fun String.trim(count: Int): String {
    return when {
        this.isNullOrEmpty() -> this
        this.length <= count -> this
        else -> this.substring(0, count)
    }
}


/**
 * Converts a string to a "soft" id that has "_" instead of spaces.
 * e.g: "abc& $[]123" = "abc_123"
 */
fun String.toId(lowerCase: Boolean = true): String {
    val trimmed = this.trim()
    val filtered = trimmed.filter { it.isDigit() || it.isLetter() || it == ' ' || it == '-' || it == '_' || it == '.' }
    val converted = if (lowerCase) filtered.toLowerCase() else filtered
    val replaced = converted.replace(' ', '_')
    val finalText = if (replaced.isNullOrBlank()) "_" else replaced
    return finalText
}

/**
 * Converts a string to a UUID
 */
fun String.toUUId(): UUID {
    return UUID.fromString(this)
}

/**
 * Converts a string to a "soft" id that has "_" instead of spaces.
 * e.g: "abc& $[]123" = "abc&_$[]123"
 */
fun String.toUUIdOrCreate(): UUID {
    return if(this.trim().isEmpty()) UUID.randomUUID() else UUID.fromString(this)
}

/**
 * Converts a string to an identifier with only numbers, letters, '-' and '_'
 * e.g: "abc& $[]123" = "abc"
 */
fun String.toIdent(lowerCase: Boolean = true): String {
    val trimmed = if (lowerCase) this.trim().toLowerCase() else this.trim()
    val filtered = trimmed.filter { it.isDigit() || it.isLetter() || it == '-' || it == '_' || it == ' ' }
    val cleaned = if (filtered.isNullOrBlank()) "_" else filtered
    return cleaned.replace(' ', '_')
}

/**
 * Decrypts the text inside the value if value is "@{decrypt('abc')}"
 * @param value : The value containing an optin @{decrypt function
 * @param decryptor : The callback to handle the decryption
 * @return
 */
fun String.decrypt( decryptor: ((String) -> String)? = null): String =
        Strings.interpret(this, "decrypt", decryptor)

/**
 * Interprets the text as an environment variable if value is "@{env('abc')}"
 * @param value : The value containing an optin @{env function
 * @return
 */
fun String.getEnv(): String {
    return Strings.interpret(this, "env") { name ->
        System.getenv(name)
    }
}

/**
 * Interprets the text as an environment variable if value is "@{env('abc')}"
 * @param value : The value containing an optin @{env function
 * @return
 */
fun String.env(): String {
    return System.getenv(this)
}

/**
 * Gets the value if non-null AND non-empty otherwise default value.
 */
fun String?.orElse(defaultVal: String): String {
    return when (this) {
        null -> defaultVal
        "" -> defaultVal
        else -> if (this.isEmpty()) defaultVal else this
    }
}

fun String.escapeHtml(): String {
    val escapedTxt = StringBuilder()
    for (i in 0..this.length - 1) {
        val tmp = this[i]
        when (tmp) {
            '<' -> escapedTxt.append("&lt;")
            '>' -> escapedTxt.append("&gt;")
            '&' -> escapedTxt.append("&amp;")
            '"' -> escapedTxt.append("&quot;")
            '\'' -> escapedTxt.append("&#x27;")
            '/' -> escapedTxt.append("&#x2F;")
            else -> escapedTxt.append(tmp)
        }
    }
    return escapedTxt.toString()
}

/**
 * Splits the string to a map using the converters supplied
 * "a,b"     = map( "a" to "a", "b" to "b" )
 * "a=1,b=2" = map( "a" to 1  , "b" to 2 )
 */
fun String.splitToMapOfType(
        delimeterPairs: Char = ',',
        trim: Boolean = true,
        delimiterValue: Char? = '=',
        keyConverter: ((String) -> Any)? = null,
        valConverter: ((String) -> Any)? = null
): Map<*, *> {
    return if (this.isNullOrEmpty()) {
        mapOf<Any, Any>()
    } else {
        val pairs = this.split(delimeterPairs)
        val map = mutableMapOf<Any, Any>()
        for (pair in pairs) {
            val keyVal: Pair<String, String> = delimiterValue?.let { d ->
                val tokens = pair.split(d)
                Pair(tokens[0], tokens[1])
            } ?: Pair(pair, pair)

            val pkey = if (trim) keyVal.first.trim() else keyVal.first
            val pval = if (trim) keyVal.second.trim() else keyVal.second
            val finalKey = keyConverter?.let { k -> k(pkey) } ?: pkey
            val finalVal = valConverter?.let { c -> c(pval) } ?: pval
            map.put(finalKey, finalVal)
        }
        map.toMap()
    }
}

/**
 * Splits the string to a map
 * "a,b"     = map( "a" to "a", "b" to "b" )
 * "a=1,b=2" = map( "a" to 1  , "b" to 2 )
 */
fun String.splitToMapWithPairs(delimiterPairs: Char = ',', delimiterKeyValue: Char = '=', trim: Boolean = true): Map<String, String> {
    val map = mutableMapOf<String, String>()
    if (!this.isNullOrEmpty()) {
        val pairs = this.split(delimiterPairs)
        for (pair in pairs) {
            val finalPair = if (trim) pair.trim() else pair
            val tokens = finalPair.split(delimiterKeyValue)
            val key = if (trim) tokens[0].trim() else tokens[0]

            val kval = if (tokens.size > 1) {
                if (trim) tokens[1].trim() else tokens[1]
            } else {
                key
            }
            map[key] = kval
        }
    }
    return map.toMap()
}


/**
 * Gets a substring as a single Pair.
 * @example:
 * "user://appdir/log.txt".subString("://") -> Pair("user", "appdir/log.txt")
 */
fun String.subStringPair(pattern: String): Pair<String, String>? {
    return if (!this.isNullOrEmpty() && !pattern.isNullOrEmpty()) {
        val ndxPattern = this.indexOf(pattern)
        if (ndxPattern < 0) {
            null
        } else {
            val part1 = this.substring(0, ndxPattern + pattern.length)
            val remainder = this.substring(ndxPattern + pattern.length)
            Pair(part1, remainder)
        }
    } else
        null
}


fun String.intAfter(text:String):Int {
    val value = this.substring(text.length).toInt()
    return value
}


fun String.intAfterLast(text:String):Int {
    val ndxSeparator = this.lastIndexOf(text)
    val value = when(ndxSeparator >= 0) {
        true  -> this.substring(ndxSeparator + 1).toInt()
        false -> -1
    }
    return value
}


fun String.toSentenceCase():String {
    return when(this.length) {
        0 -> ""
        1 -> this[0].toUpperCase().toString()
        else -> this[0].toUpperCase() + this.substring(1)
    }
}

fun String.removeNewLines(replacement:String = " "):String {
    return this.replace("\r\n", replacement).replace("\n", replacement)
}


fun String.toTime(text:String): LocalTime {
    return try {
        val num = text.toInt()
        toTime(num)
    }
    catch(ex:Exception) {
        LocalTime.of(12, 0, 0)
    }
}


fun String.toTime(numval:Int):LocalTime {
    // nothing
    if (numval == 0)
        return LocalTime.of(0, 0, 0)

    // 8:30:00 am = 83000
    // 8:30:00 pm = 203000
    // 12:30:00 am = 03000
    var value = numval.toString()
    if (value.length < 5)
        value = "00"
    if (value.length < 6)
        value = "0$value"
    val hours = value.substring(0, 2).toInt()
    val mins = value.substring(2, 4).toInt()
    val secs = value.substring(4, 6).toInt()
    return LocalTime.of(hours, mins, secs, 0)
}