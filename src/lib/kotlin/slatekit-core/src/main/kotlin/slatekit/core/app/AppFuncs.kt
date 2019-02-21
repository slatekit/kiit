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

package slatekit.core.app

import slatekit.common.*
import slatekit.common.args.Args
import slatekit.common.args.ArgsFuncs
import slatekit.common.args.ArgsFuncs.isExit
import slatekit.common.args.ArgsFuncs.isVersion
import slatekit.common.conf.*
import slatekit.common.conf.ConfFuncs.CONFIG_DEFAULT_PROPERTIES
import slatekit.common.conf.ConfFuncs.CONFIG_DEFAULT_SUFFIX
import slatekit.common.encrypt.Encryptor
import slatekit.common.envs.*
import slatekit.common.info.*
import slatekit.common.log.*
import slatekit.core.common.AppContext
import slatekit.results.Failure
import slatekit.results.Notice
import slatekit.results.Success

object AppFuncs {


    fun getConfPath(args: Args, file: String, conf: Conf?): String {
        val pathFromArgs = args.getStringOrElse("conf.dir", "")
        val location = pathFromArgs.orElse( conf?.getStringOrElse("conf.dir", "") ?: "")
        val prefix = when (location) {
            "jars" -> ""
            "conf" -> "file://./conf/"
            "" -> ""
            else -> location
        }
        return prefix + file
    }

    /**
     * Checks the command for either an instructions about app or for exiting:
     * 1. exit
     * 2. version
     * 3. about
     *
     * @param raw
     * @return
     */
    fun isMetaCommand(raw: List<String>): Notice<String> {

        // Case 1: Exit ?
        return if (isExit(raw, 0)) {
            Success("exit", EXIT)
        }
        // Case 2a: version ?
        else if (isVersion(raw, 0)) {
            Success("version")
        }
        // Case 2b: about ?
        // Case 3a: Help ?
        else if (ArgsFuncs.isAbout(raw, 0) || ArgsFuncs.isHelp(raw, 0)) {
            Success("about")
        } else {
            Failure("other")
        }
    }

    /**
     * gets the selected environment by key "env" from command line args first or env.conf second
     *
     * @return
     */
    fun getEnv(args: Args, conf: Conf): Env {
        val env = getConfOverride(args, conf, "env", "loc")
        return Env.parse(env)
    }

    /**
     * gets log level by key "log.level" from command line args first or environment config 2nd
     *
     * @return
     */
    fun getLogLevel(args: Args, conf: Conf): LogLevel {
        val level = getConfOverride(args, conf, "log.level", "info")
        return LogHelper.parseLevel(level)
    }

    /**
     * gets log name by key "log.name" from command line args first or environment config 2nd
     *
     * @return
     */
    fun getLogName(args: Args, conf: Conf): String {
        val log = getConfOverride(args, conf, "log.name", "@{app}-@{env}-@{date}.log")
        return log
    }

    fun getConfOverride(args: Args, conf: Conf, key: String, defaultValue: String?): String {

        val finalDefaultValue = defaultValue ?: ""

        // 1. From cmd line args
        val arg = args.getStringOrElse(key, "")

        // 2. From env.conf ( or respective environment config )
        val cfg = conf.getStringOrElse(key, finalDefaultValue)

        // 3. Cmd line override
        return if (!arg.isNullOrEmpty())
            arg
        else
            cfg ?: finalDefaultValue
    }

    fun buildAppInputs(args: Args, enc: Encryptor?): Notice<AppInputs> {
        // 1. Load the base conf "env.conf" from the directory specified.
        // or specified in the "conf.dirs" config setting in the env.conf file
        // a) -conf="jars"                  = embedded in jar files
        // b) -conf="conf"                  = expect directory ./conf
        // c) -conf="file://./conf-samples  = expect directory ./conf-samples
        // d) not specified = defaults to jars.
        // NOTES:
        // 1. The location of the directory can be over-riden on the command line
        // 2. The conf base is loaded again since if the "-help" arg was supplied
        // if will get the info from the confBase ( env.conf )
        val confBase = Config(getConfPath(args, CONFIG_DEFAULT_PROPERTIES, null), enc)

        // 2. The environment can be selected in the following order:
        // - command line ( via "-env=dev"   )
        // - env.conf ( via env.name = dev )
        // getEnv will first look for selected environment from args, then in config.
        val envSelected = getEnv(args, confBase)

        // 2. Validate the environment
        // Get all
        val allEnvs = AppBuilder.envs()
        val envCheck = Envs(allEnvs, allEnvs.firstOrNull()).validate(envSelected)
        val envName = envSelected.name

        return envCheck?.let { env ->
            // 4. We now have the environment to use ( e.g. "dev" )
            // Now load the final environment specific override
            // for directory reference provide: "file://./conf/"
            val overrideConfPath = getConfPath(args, "env.${env.name}" + CONFIG_DEFAULT_SUFFIX, confBase)
            val confEnv = ConfigMulti(overrideConfPath, confBase, enc)

            Success(AppInputs(args, envCheck, confBase, confEnv))
        } ?: Failure("Unknown environment name : $envName supplied")
    }

    fun buildContext(appInputs: AppInputs, enc: Encryptor?, logs: Logs?): AppContext {

        val buildInfoExists = resourceExists("build.conf")
        val build = if (buildInfoExists) {
            val stamp = Config(getConfPath(appInputs.args, "build.conf", null), enc)
            val info = stamp.buildStamp("build")
            info
        } else {
            Build.empty
        }

        val args = appInputs.args
        val env = appInputs.env

        // The config is inheritance based.
        // Which means the base env.loc.conf inherits from env.conf.
        val conf = ConfigMulti(
                appInputs.confEnv,
                appInputs.confBase,
                enc)

        return AppContext(
                arg = args,
                env = env,
                cfg = conf,
                enc = enc,
                logs = logs ?: LogsDefault,
                app = AppBuilder.about(conf),
                sys = Sys.build(),
                build = build,
                start = StartInfo(args.line, env.key, conf.origin(), env.key),
                dirs = AppBuilder.folders(conf)
        )
    }

    fun resourceExists(path: String): Boolean {
        val res = this.javaClass.getResource("/$path")
        return res != null
    }
}
