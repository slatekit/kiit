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
 * NOTE: The type-safe versions of theses constants are located in:
 * 1. ApiProtocol.scala
 * 2. ApiAuthMode.scala
 * 3. ApiRoleRefs.scala
 *
 * These are only available here as constants to allow checking against
 * 1. Api's setup via annotations ( in which case they have to reference strings )
 * 2. Api's setup via configuration files ( again in which case they have to reference strings )
 */
object ApiConstants {
    val ProtocolAny = "*"
    val ProtocolCLI = "cli"
    val ProtocolWeb = "web"

    val RoleAny = "*"
    val RoleGuest = "?"
    val RoleParent = "@parent"
    val RoleNone = "@none"

    val AuthModeAppKey = "app-key"
    val AuthModeAppRole = "app-roles"
    val AuthModeKeyRole = "key-roles"
}

