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

package slatekit.common.security

/**
 * Represents credentials for accessing an API. This is useful for representing credentials
 * for other systems such as AWS keys, Twilio, SendGrid, etc.
 * @param account : The account for the API
 * @param key : The key for the API
 * @param pass : The password for the API  ( optional )
 * @param env : Optional environment of the API ( e.g. dev, qa )
 * @param tag : Optional tag
 */
data class ApiLogin(
    val account: String = "",
    val key: String = "",
    val pass: String = "",
    val env: String = "",
    val tag: String = ""
) {

    companion object {
        @JvmStatic
        val empty = ApiLogin()
    }
}
