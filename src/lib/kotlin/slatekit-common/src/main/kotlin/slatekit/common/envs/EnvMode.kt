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

package slatekit.common.envs

abstract class EnvMode(val name: String)

/**
 * production
 */
object Prod : EnvMode("pro")


/**
 * Development
 */
object Dev : EnvMode("dev")


/**
 * Quality assurance
 */
object Qa : EnvMode("qa")


/**
 * User Acceptance / Beta
 */
object Uat : EnvMode("uat")


/**
 * Disaster recovery
 */
object Dis : EnvMode("dr")


/**
 * Other environment mode
 * @param m
 */
class Other(m: String) : EnvMode(m)
