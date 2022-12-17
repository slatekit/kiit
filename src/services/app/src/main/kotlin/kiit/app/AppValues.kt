package kiit.app

import slatekit.common.args.Args
import slatekit.common.conf.Conf
import slatekit.common.envs.Env
import slatekit.common.log.LogLevel

/**
 * Handles getting values/settings from either command line args or config
 * The command line args override the values from conf
 */
class AppValues(val args: Args, val conf: Conf) {

    /**
     * gets the selected environment by key "env" from command line args first or env.conf second
     *
     * @return
     */
    fun env(): Env {
        val env = getOverride("env", "loc")
        return Env.parse(env)
    }

    /**
     * gets log level by key "log.level" from command line args first or environment config 2nd
     *
     * @return
     */
    fun logLevel(): LogLevel {
        val level = getOverride("log.level", "info")
        return LogLevel.parse(level)
    }

    /**
     * gets log name by key "log.name" from command line args first or environment config 2nd
     *
     * @return
     */
    fun logName(): String {
        val log = getOverride("log.name", "@{app}-@{env}-@{date}.log")
        return log
    }

    /**
     * Gets the config setting override
     */
    fun getOverride(key: String, defaultValue: String?): String {

        val finalDefaultValue = defaultValue ?: ""

        // 1. From cmd line args
        val arg = args.getStringOrElse(key, "")

        // 2. From env.conf ( or respective environment config )
        val cfg = conf.getStringOrElse(key, finalDefaultValue)

        // 3. Cmd line override
        return if (!arg.isNullOrEmpty())
            arg
        else
            cfg
    }
}
