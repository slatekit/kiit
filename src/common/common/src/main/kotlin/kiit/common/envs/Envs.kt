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

/**
 * Store the currently selected environment ( local, dev, qa, stg, prod ) and provides some
 * utility functions to parse an environment
 */
data class Envs(val all: List<Env>, val current: Env) : EnvSupport {

    /**
     * Initialize with the first one
     */
    constructor(all: List<Env>) : this(all, all.first{ it.isDev })

    /**
     * Name of the currently selected environment e.g. ( dev1, qa1, qa2, beta, prod )
     * @return
     */
    val name: String get() = current.name

    /**
     * Environment of the currently selected ( dev, qa, uat, pro )
     * @return
     */
    val env: String get() = current.mode.name

    /**
     * Env Mode of the currently selected ( dev, qa, uat, pro )
     * @return
     */
    val mode: EnvMode get() = current.mode

    /**
     * The fully qualified name of the currently selected environment ( combines the name + key )
     * @return
     */
    val key: String get() = current.key

    /**
     * whether the current environment matches the environment name supplied.
     * @param envMode
     * @return
     */
    override fun isEnvName(envMode: String): Boolean = this.env == envMode

    /**
     * whether the current environment matches the environment  supplied.
     * @param envMode
     * @return
     */
    override fun isEnv(envMode: EnvMode): Boolean = this.env == envMode.name

    /**
     * selects a new environment and returns a new Envs collection with the s
     * selected environment
     * @param name : Name of the environment
     * @return
     */
    fun select(name: String): Envs {
        val matched = all.filter { item -> item.name == name }
        return Envs(all, matched.first())
    }

    /**
     * validates the environment against the supported
     *
     * @param env
     * @return
     */
    fun validate(env: Env): Env? = this.get(env.name)

    /**
     * validates the environment against the supported
     *
     * @param name
     * @return
     */
    fun isValid(name: String): Boolean = this.get(name) != null

    /**
     * validates the environment against the supported
     *
     * @param name
     * @return
     */
    fun get(name: String): Env? = all.firstOrNull { item -> item.name == name }


    companion object {
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
         * /resources/env.qat.conf ( qat         config )
         * /resources/env.stg.conf ( staging     config )
         * /resources/env.pro.conf ( production  config )
         *
         * e.g. For multiple QAT environments ( qa1, qa2 )
         * /resources/env.qa1.conf ( qa1         config )
         * /resources/env.qa2.conf ( qa2         config )
         *
         * @return
         */
        @JvmStatic
        fun defaults(selected:String? = null): Envs {
            val envs = Envs(listOf(
                    Env("loc", EnvMode.Dev, desc = "Dev environment (local)"),
                    Env("dev", EnvMode.Dev, desc = "Dev environment (shared)"),
                    Env("qat", EnvMode.Qat, desc = "QA environment  (current release)"),
                    Env("stg", EnvMode.Uat, desc = "STG environment (demo)"),
                    Env("pro", EnvMode.Pro, desc = "LIVE environment")
            ))
            return selected?.let { envs.select(it) } ?: envs
        }
    }
}