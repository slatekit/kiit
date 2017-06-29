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

interface EnvSupport {


    val isProd: Boolean get() = isEnv(Prod)


    val isUat: Boolean get() = isEnv(Uat)


    val isQa: Boolean get() = isEnv(Qa)


    val isDev: Boolean get() = isEnv(Dev)


    val isDis: Boolean get() = isEnv(Dis)


    fun isEnv(envMode: EnvMode): Boolean


    fun isEnvName(envMode: String): Boolean
}
