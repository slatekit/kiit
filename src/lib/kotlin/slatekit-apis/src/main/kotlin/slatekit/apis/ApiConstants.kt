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
 * NOTE: The could be more type-safe but are needed as string for the annotations.
 *
 * These are only available here as constants to allow checking against
 * 1. Api's setup via annotations ( in which case they have to reference strings )
 * 2. Api's setup via configuration files ( again in which case they have to reference strings )
 */
object ApiConstants {
    const val Version = "1.0"
    const val SourceAny = "*"

    const val SourceCLI = "cli"
    const val SourceWeb = "web"
    const val SourceFile = "file"
    const val SourceQueue = "queue"

    const val Any = "*"
    const val None = ""
    const val Unknown = "?"
    const val Parent = "@parent"

    const val AuthModeAppKey = "app-key"
    const val AuthModeAppRole = "app-roles"
    const val AuthModeKeyRole = "key-roles"
}

