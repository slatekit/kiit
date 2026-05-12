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

import kotlin.js.JsExport

@JsExport
sealed class EnvMode(val name: String) {

    /**
     * Development
     */
    object Dev : EnvMode("dev")

    /**
     * Quality assurance
     */
    object Qat : EnvMode("qat")

    /**
     * User Acceptance / Beta
     */
    object Uat : EnvMode("uat")

    /**
     * Disaster recovery
     */
    object Dis : EnvMode("dis")

    /**
     * production
     */
    object Pro : EnvMode("pro")

    /**
     * Other environment mode
     * @param m
     */
    class Other(m: String) : EnvMode(m)

    companion object {

        fun parse(text: String): EnvMode = when (text.trim().lowercase()) {
            Dev.name -> Dev
            Qat.name -> Qat
            Uat.name -> Uat
            Dis.name -> Dis
            Pro.name -> Pro
            else -> Other(text)
        }
    }
}
