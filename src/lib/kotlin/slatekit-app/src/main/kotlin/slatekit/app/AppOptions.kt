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

package slatekit.app

open class AppOptions(
    val showWelcome: Boolean = true,
    val showDisplay: Boolean = true,
    val showSummary: Boolean = true,
    val getConfNameFromEnv: Boolean = true,
    val getLogNameFromEnv: Boolean = true
)
