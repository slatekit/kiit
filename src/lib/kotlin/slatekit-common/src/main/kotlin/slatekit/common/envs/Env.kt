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


/**
 * Represents a system environment
 * @param name   : e.g. Quality Assurance
 * @param mode   : e.g. Qa
 * @param region : new york
 * @param desc   : Qa environment 1 in new york
 */
data class Env(val name: String,
               val mode: EnvMode,
               val region: String = "",
               val desc: String = "") : EnvSupport {

    /**
     * "qa1:qa"
     * @return
     */
    val key: String get() = name + ":" + mode.name


    override fun isEnvName(envMode: String): Boolean = mode.name == envMode


    override fun isEnv(envMode: EnvMode): Boolean = mode == envMode


    companion object {

        val empty = Env("", Dev, "", "")


        /**
         * The list of defaults environments to choose from.
         * An environment definition is defined by its name, mode
         * The key is built up from name and mode as {name}.{mode}
         * e.g. "qa1.QA"
         *
         * Each of these environments should map to an associated env.{name}.conf
         * config file in the /resources/ directory. But there is no dependency
         * on this Env component to a Config component
         *
         * e.g.
         * /resources/env.conf     ( common      config )
         * /resources/env.loc.conf ( local       config )
         * /resources/env.dev.conf ( development config )
         * /resources/env.qa1.conf ( qa1         config )
         * /resources/env.qa2.conf ( qa2         config )
         * /resources/env.stg.conf ( staging     config )
         * /resources/env.pro.conf ( production  config )
         *
         * @return
         */
        fun defaults(): Envs =
            Envs(listOf(
                Env("loc", Dev , desc = "Dev environment (local)"),
                Env("dev", Dev , desc = "Dev environment (shared)"),
                Env("qa1", Qa  , desc = "QA environment  (current release)"),
                Env("qa2", Qa  , desc = "QA environment  (last release)"),
                Env("stg", Uat , desc = "STG environment (demo)"),
                Env("pro", Prod, desc = "LIVE environment")
            ))



        /**
         * parses the environment name e.g. "qa1:qa" = name:mode
         * @param env
         * @return
         */
        fun parse(text: String): Env {
            if (text == "''" || text == "\"\"") {
                return Env("", Dev, desc = "Default environment")
            }
            val tokens = text.split(':')

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

