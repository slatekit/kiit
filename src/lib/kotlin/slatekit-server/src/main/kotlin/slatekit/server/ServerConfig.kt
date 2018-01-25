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

package slatekit.server


/**
 * @param port     : The port number to use
 * @param prefix   : An optional prefix for all the API routes e..g "/api/"
 * @param info     : Whether to show info about the server on startup.
 * @param cors     : Whether or not to enable cors
 * @param docs     : Whether or not to enable help docs
 * @param docKey   : The doc api key needed to authorize help docs
 * @param static   : Whether or not to host static files
 * @param staticDir: The directory of the static files
 *                NOTE: This is either the "doc-key" in the header or query params
 */
data class ServerConfig(
        val port     : Int     = 5000,
        val prefix   : String  = "/api/",
        val info     : Boolean = true,
        val cors     : Boolean = false,
        val docs     : Boolean = false,
        val docKey   : String  = ""   ,
        val static   : Boolean = false,
        val staticDir: String  = "",
        val setup    : ((Any)  -> Unit)? = null
)