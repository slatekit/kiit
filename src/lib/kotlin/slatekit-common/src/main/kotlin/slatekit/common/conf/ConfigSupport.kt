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

import slatekit.common.ApiLogin
import slatekit.common.Credentials
import slatekit.common.db.DbCon
import slatekit.common.db.DbConEmpty
import slatekit.common.db.DbConString
import slatekit.common.envs.Env
import slatekit.common.info.Build

interface ConfigSupport {

    fun config(): ConfigBase


    /**
     * gets the environment specified in the config via "env.name,env.mode"
     *
     * @return
     */
    fun env(): Env =

            mapTo("env", { conf ->

                val name = conf.getString("env.name")
                val mode = conf.getString("env.mode")
                Env(name, Env.interpret(mode), "$mode : $name")
            }) ?: Env.empty


    /**
     * Gets user credentials from the config.
     * This is used for the CLI ( command line interface ) shell.
     *
     * @return
     */
    fun login(name: String): Credentials =

            mapTo(name, { conf ->

                Credentials(
                        conf.getString(name + ".id"),
                        conf.getString(name + ".name"),
                        conf.getString(name + ".email"),
                        conf.getString(name + ".key"),
                        conf.getString(name + ".env"),
                        conf.getString(name + ".region")
                )
            }) ?: Credentials.empty


    /**
     * Gets build stamp info
     *
     * @return
     */
    fun buildStamp(name: String): Build =

            mapTo(name, { conf ->

                Build(
                        conf.getString(name + ".version"),
                        conf.getString(name + ".commit"),
                        conf.getString(name + ".branch"),
                        conf.getString(name + ".date")
                )
            }) ?: Build.empty

    /**
     * Gets user credentials from the config.
     * This is used for the CLI ( command line interface ) shell.
     *
     * @return
     */
    fun apiKey(name: String): ApiLogin =

            mapTo(name, { conf ->

                ApiLogin(
                        conf.getString(name + ".account"),
                        conf.getString(name + ".key"),
                        conf.getString(name + ".pass"),
                        conf.getString(name + ".env"),
                        conf.getString(name + ".tag")
                )
            }) ?: ApiLogin.empty


    /**
     * connection string from the config
     *
     * @param prefix
     * @return
     */
    fun dbCon(prefix: String = "db"): DbCon =

            mapTo(prefix, { conf ->
                DbConString(
                        conf.getString(prefix + ".driver"),
                        conf.getString(prefix + ".url"),
                        conf.getString(prefix + ".user"),
                        conf.getString(prefix + ".pswd")
                )
            }) ?: DbConEmpty
}


fun <T> ConfigSupport.mapTo(key: String, mapper: (ConfigBase) -> T): T? =

        // Section not present!
        if (config().containsKey(key)) {
            // Location specified ?
            val locationKey = key + ".location"
            if (config().containsKey(locationKey)) {

                // 1. "@{resource}/sms.conf"
                // 2. "@{company.dir}/sms.conf"
                // 3. "@{app.dir}/sms.conf"
                // 3. "/conf/sms.conf"
                val location = config().getString(locationKey)
                val conf: ConfigBase? = config().loadFrom(location)
                conf?.let { c -> mapper(c) }
            }
            else
                mapper(config())
        }
        else
            null


