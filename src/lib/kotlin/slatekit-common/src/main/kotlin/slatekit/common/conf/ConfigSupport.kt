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

package slatekit.common.conf

import slatekit.common.info.ApiLogin
import slatekit.common.info.Credentials
import slatekit.common.data.DbCon
import slatekit.common.data.DbConString
import slatekit.common.envs.Env
import slatekit.common.info.Build

interface ConfigSupport {

    fun config(): Conf

    /**
     * gets the environment specified in the config via "env.name,env.mode"
     *
     * @return
     */
    fun env(): Env =
            mapTo("env") { conf ->
                val name = conf.getString("env.name")
                val mode = conf.getString("env.mode")
                Env(name, Env.interpret(mode), "$mode : $name")
            } ?: Env.empty

    /**
     * Gets user credentials from the config.
     * This is used for the CLI ( command line interface ) shell.
     *
     * @return
     */
    fun login(name: String): Credentials =
            mapTo(name) { conf ->
                Credentials(
                        conf.getString(name + ".id"),
                        conf.getString(name + ".name"),
                        conf.getString(name + ".email"),
                        conf.getString(name + ".key"),
                        conf.getString(name + ".env"),
                        conf.getString(name + ".region")
                )
            } ?: Credentials.empty

    /**
     * Gets build stamp info
     *
     * @return
     */
    fun buildStamp(name: String): Build =
            mapTo(name) { conf ->

                Build(
                        conf.getString(name + ".version"),
                        conf.getString(name + ".commit"),
                        conf.getString(name + ".branch"),
                        conf.getString(name + ".date")
                )
            } ?: Build.empty

    /**
     * Gets user credentials from the config.
     * This is used for the CLI ( command line interface ) shell.
     *
     * @return
     */
    fun apiLogin(name: String, prefix: String? = null): ApiLogin {

        val finalPrefix = prefix ?: name
        return mapTo(name) { conf ->

            ApiLogin(
                    conf.getString("$finalPrefix.account"),
                    conf.getString("$finalPrefix.key"),
                    conf.getString("$finalPrefix.pass"),
                    conf.getString("$finalPrefix.env"),
                    conf.getString("$finalPrefix.tag")
            )
        } ?: ApiLogin.empty
    }

    /**
     * connection string from the config
     *
     * @param prefix
     * @return
     */
    fun dbCon(prefix: String = "db"): DbCon =

            mapTo(prefix) { conf ->
                DbConString(
                        conf.getString(prefix + ".driver"),
                        conf.getString(prefix + ".url"),
                        conf.getString(prefix + ".user"),
                        conf.getString(prefix + ".pswd")
                )
            } ?: DbCon.empty
}

fun <T> ConfigSupport.mapTo(key: String, mapper: (Conf) -> T): T? {

    // Reference to file location.
    // e.g. db.location = "user://.slatekit/tools/apis/conf/db.conf"
    val locationKey = "$key.location"

    // Section not present!
    return if (config().containsKey(locationKey)) {

        // 1. "@{resource}/sms.conf"
        // 2. "@{company.dir}/sms.conf"
        // 3. "@{app.dir}/sms.conf"
        // 3. "/conf/sms.conf"
        val location = config().getString(locationKey)
        val conf: Conf? = config().loadFrom(location)
        conf?.let { c -> mapper(c) }
    } else {
        mapper(config())
    }
}
