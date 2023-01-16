/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *
 * 
  *  </kiit_header>
 */

package kiit.common.info

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
    @JvmField val account: String = "",
    @JvmField val key: String = "",
    @JvmField val pass: String = "",
    @JvmField val env: String = "",
    @JvmField val tag: String = ""
) {

    companion object {
        @JvmStatic
        val empty = ApiLogin()
    }
}
