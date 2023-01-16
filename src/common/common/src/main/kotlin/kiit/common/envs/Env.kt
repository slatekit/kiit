/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github 
 *  </kiit_header>
 */

package kiit.common.envs

/**
 * Represents a system environment
 * @param name : e.g. Quality Assurance test
 * @param mode : e.g. Qat
 * @param region : new york
 * @param desc : Qat environment 1 in new york
 */
data class Env(
    @JvmField
    val name: String,

    @JvmField
    val mode: EnvMode,

    @JvmField
    val region: String = "",

    @JvmField
    val desc: String = ""
) : EnvSupport {

    /**
     * "qa1:qa"
     * @return
     */
    val key: String get() = name + ":" + mode.name

    override fun isEnvName(envMode: String): Boolean = mode.name == envMode

    override fun isEnv(envMode: EnvMode): Boolean = mode == envMode

    companion object {

        @JvmStatic
        val empty = Env("", EnvMode.Dev, "", "")


        /**
         * parses the environment name e.g. "qa1:qa" = name:mode
         * @param env
         * @return
         */
        @JvmStatic
        fun parse(text: String): Env {
            if (text == "''" || text == "\"\"") {
                return Env("", EnvMode.Dev, desc = "Default environment")
            }
            val tokens = text.split(':')

            // e.g. "dev1", "dev", dev1:dev")
            val env = if (tokens.size == 1)
                Env(tokens[0], interpret(tokens[0]))
            else
                Env(tokens[0], interpret(tokens[1]))

            return env
        }

        /**
         * interprets the string representation of an environment into the type object
         * @param mode
         * @return
         */
        @JvmStatic
        fun interpret(mode: String): EnvMode =
                when (mode) {
                    EnvMode.Dev.name -> EnvMode.Dev
                    EnvMode.Qat.name -> EnvMode.Qat
                    EnvMode.Uat.name -> EnvMode.Uat
                    EnvMode.Pro.name -> EnvMode.Pro
                    EnvMode.Dis.name -> EnvMode.Dis
                    else -> EnvMode.Other(mode)
                }
    }
}
