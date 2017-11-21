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

import java.time.format.DateTimeFormatter


object InputFuncs {

    fun interpret(value: String, functionName: String, handler: ((String) -> String)?): String {
        return if (value.startsWith("@{$functionName('")) {
            val end = value.indexOf("')}")
            val paramVal = value.substring(4 + functionName.length, end)
            handler?.invoke(paramVal) ?: paramVal
        }
        else
            value
    }


    /**
     * Decrypts the text inside the value if value is "@{decrypt('abc')}"
     * @param value     : The value containing an optin @{decrypt function
     * @param decryptor : The callback to handle the decryption
     * @return
     */
    fun decrypt(value: String, decryptor: ((String) -> String)? = null): String =
            interpret(value, "decrypt", decryptor)


    /**
     * converts a date string to a Date with support for aliases such as "today"
     * @param value
     * @return
     */
    fun convertDate(value: String): DateTime =
        when (value) {
            ""             -> DateTime.now()
            "@{today}"     -> DateTime.today()
            "@{tomorrow}"  -> DateTime.today().plusDays(1)
            "@{yesterday}" -> DateTime.today().plusDays(-1)
            "@{now}"       -> DateTime.now()
            else           -> DateTime.parse(value)
        }


}
