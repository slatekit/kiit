/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.common

/**
 * NOTE: Using object here due to STRANGE compile issues
 * /w extension functions ( from library modules )
 * being used downstream in client libraries.
 */
object Strings {

    /**
     * returns a new string that truncates the string supplied by the count of chars.
     */
    fun truncate(text: String, count: Int): String {
        return when {
            text.isNullOrEmpty() -> text
            text.length <= count -> text
            else -> text.substring(0, count)
        }
    }


    /**
     * Interprets a string which could have a macro function inside it.
     * NOTE: This is currently used for decryption of config settings at runtime
     * @param value   : "@{decrypt('abc123')"
     * @param macro   : "decrypt", the name of the expected macro
     * @param handler : lambda to handle execute the macro given the parameter
     */
    fun interpret(value: String, macro: String, handler: ((String) -> String)?): String {
        return if (value.startsWith("@{$macro('")) {
            val end = value.indexOf("')}")
            val paramVal = value.substring(4 + macro.length, end)
            handler?.invoke(paramVal) ?: paramVal
        } else
            value
    }


    /**
     * Decrypts the text inside the value if value is "@{decrypt('abc')}"
     * @param value : The value containing an optin @{decrypt function
     * @param decryptor : The callback to handle the decryption
     * @return
     */
    fun decrypt(value: String, decryptor: ((String) -> String)? = null): String =
            Strings.interpret(value, "decrypt", decryptor)




    /**
     * String match factoring in the wildcard "*"
     */
    fun isMatchOrWildCard(actual: String, expected: String, wildcard:String = "*"): Boolean {
        return if (actual.isNullOrEmpty() || actual == wildcard)
            true
        else
            actual == expected
    }

}


/**
 * Repeats the text using a delimiter.
 * e.g. ?.repateWith(",", 3) = "?,?,?"
 */
fun String.repeatWith(delimiter: String, count: Int): String {
    return (0 until count).foldIndexed("", { ndx, acc, _ -> acc + (if (ndx == 0) "" else delimiter) + this })
}

/**
 * Converts the text to PascalCase e.g. "userName" becomes "UserName"
 */
fun String.pascalCase(): String {
    return this[0].toUpperCase() + this.substring(1)
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

val String.toCharMap: Map<Char, Boolean> get() = this.toCharArray().map { c -> c to true }.toMap()

/**
 * Shortcut for new line
 */
val newline: String get() = System.lineSeparator()


