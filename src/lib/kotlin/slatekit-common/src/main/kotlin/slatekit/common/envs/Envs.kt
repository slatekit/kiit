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
 * Store the currently selected environment ( local, dev, qa, stg, prod ) and provides some
 * utility functions to parse an environment
 */
data class Envs(val all: List<Env>, val current: Env? = null) : EnvSupport {

    /**
     * Initialize with the first one
     */
    constructor(all:List<Env>): this(all, all.firstOrNull())


    /**
     * Name of the currently selected environment e.g. ( dev1, qa1, qa2, beta, prod )
     * @return
     */
    val name: String get() = current?.name ?: ""


    /**
     * Environment of the currently selected ( dev, qa, uat, pro )
     * @return
     */
    val env: String get() = current?.mode?.name ?: ""


    /**
     * The fully qualified name of the currently selected environment ( combines the name + key )
     * @return
     */
    val key: String get() = current?.key ?: ""


    /**
     * whether the current environment matches the environment name supplied.
     * @param env
     * @return
     */
    override fun isEnvName(envMode: String): Boolean = this.env == envMode


    /**
     * whether the current environment matches the environment  supplied.
     * @param env
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
    fun get(name: String): Env? =
            all.filter { item -> item.name == name }.firstOrNull()

}