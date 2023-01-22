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

package kiit.common.envs

interface EnvSupport {

    val isPro: Boolean get() = isEnv(EnvMode.Pro)

    val isUat: Boolean get() = isEnv(EnvMode.Uat)

    val isQat: Boolean get() = isEnv(EnvMode.Qat)

    val isDev: Boolean get() = isEnv(EnvMode.Dev)

    val isDis: Boolean get() = isEnv(EnvMode.Dis)

    val isOther: Boolean
        get() {
            return !isDev && !isQat && !isUat && !isPro && !isDis
        }

    fun isEnv(envMode: EnvMode): Boolean

    fun isEnvName(envMode: String): Boolean
}
