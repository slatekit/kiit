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

package slatekit.apis

/**
 * Created by kreddy on 3/25/2016.
 */
data class ApiSettings(
    val enableFileInputs: Boolean = false,
    val enableLogging: Boolean = false,
    val enableOutput: Boolean = false
)