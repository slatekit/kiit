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
            Dev.name -> Dev
            Qat.name -> Qat
            Uat.name -> Uat
            Dis.name -> Dis
            Pro.name -> Pro
            else     -> Other(text)
        }
    }
}

