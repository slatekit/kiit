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

        /**
         * This is needed for Java interop.
         * Currently can not set a JvmField/JvmStatic attribute on the
         * sealed classes ( Dev | Qat | Uat | Dis | Pro and also get
         * and error that the sealed classes are private to EnvMode in java
         * when trying to instantiate it.
         */
        @JvmStatic
        fun parse(text:String): EnvMode = when(text.trim().toLowerCase()) {
            EnvMode.Dev.name -> EnvMode.Dev
            EnvMode.Qat.name -> EnvMode.Qat
            EnvMode.Uat.name -> EnvMode.Uat
            EnvMode.Dis.name -> EnvMode.Dis
            EnvMode.Pro.name -> EnvMode.Pro
            else             -> EnvMode.Other(text)
        }
    }
}

