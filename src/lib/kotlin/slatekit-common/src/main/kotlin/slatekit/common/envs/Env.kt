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

import slatekit.common.Strings

/**
 * Represents a system environment
 * @param name   : e.g. Quality Assurance
 * @param mode   : e.g. Qa
 * @param region :  york
 * @param desc   : Qa environment 1 in  york
 */
data class Env(val name: String, val mode: EnvMode, val region: String = "", val desc: String = "") : EnvSupport {

    /**
     * "qa1:qa"
     * @return
     */
    val key: String get() = name + ":" + mode.name


    override fun isEnvName(envMode: String): Boolean = mode.name == envMode


    override fun isEnv(envMode: EnvMode): Boolean = mode == envMode


    companion object EnvStatic {

        val empty = Env("", Dev, "", "")


        /**
         * List of funault environments supported in slate kit
         * @return
         */
        fun defaults(): Envs {
            val all = listOf(
                    Env("loc", Dev, desc = "Dev environment (local)"),
                    Env("dev", Dev, desc = "Dev environment (shared)"),
                    Env("qa1", Qa, desc = "QA environment  (current release)"),
                    Env("qa2", Qa, desc = "QA environment  (last release)"),
                    Env("stg", Uat, desc = "STG environment (demo)"),
                    Env("pro", Prod, desc = "LIVE environment")
            )
            return Envs(all, all[0])
        }


        /**
         * parses the environment name e.g. "qa1:qa" = name:mode
         * @param env
         * @return
         */
        fun parse(text: String): Env {
            if (text == "''" || text == "\"\"") {
                return Env("", Dev, desc = "Default environment")
            }
            val tokens = Strings.split(text, ':')

            // e.g. "dev1", "dev", dev1:dev")
            val env = if (tokens.size == 1)
                Env(tokens[0], interpret(tokens[0]))
            else
                Env(tokens[0], interpret(tokens[0]))

            return env
        }


        /**
         * interprets the string representation of an environment into the type object
         * @param mode
         * @return
         */
        fun interpret(mode: String): EnvMode =
                when (mode) {
                    Dev.name  -> Dev
                    Qa.name   -> Qa
                    Uat.name  -> Uat
                    Prod.name -> Prod
                    Dis.name  -> Dis
                    else      -> Other(mode)
                }
    }
}

