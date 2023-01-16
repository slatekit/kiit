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

package kiit.common

/**
 * NOTE: Using object here due to STRANGE compile issues
 * /w extension functions ( from library modules )
 * being used downstream in client libraries.
 */
object Strings {

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

val String.toCharMap: Map<Char, Boolean> get() = this.toCharArray().map { c -> c to true }.toMap()

/**
 * Shortcut for new line
 */
val newline: String get() = System.lineSeparator()


