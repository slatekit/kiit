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

package slatekit.app

import slatekit.common.args.Args
import slatekit.common.args.ArgsCheck
import slatekit.common.args.ArgsCheck.isExit
import slatekit.common.args.ArgsCheck.isVersion
import slatekit.common.args.ArgsSchema
import slatekit.common.conf.*
import slatekit.common.conf.ConfFuncs.CONFIG_DEFAULT_SUFFIX
import slatekit.common.encrypt.Encryptor
import slatekit.common.envs.Env
import slatekit.common.envs.Envs
import slatekit.common.info.About
import slatekit.common.info.Build
import slatekit.common.info.Sys
import slatekit.common.io.Alias
import slatekit.common.io.Uri
import slatekit.common.io.Uris
import slatekit.common.log.Logs
import slatekit.common.log.LogsDefault
import slatekit.common.log.LogLevel
import slatekit.results.*

object AppUtils {

    fun getScheme(args: Args, default:Alias): Alias {
        val dirFromArgs = args.getStringOrNull("conf.dir")
        return dirFromArgs?.let{ Alias.parse(it) } ?: default
    }

    fun getDir(args: Args, default:Alias): Uri {
        val dirFromArgs = args.getStringOrNull("conf.dir")
        return Uris.parse(dirFromArgs ?: default.name)
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
    fun isMetaCommand(raw: List<String>): Outcome<String> {

        return when {
            // Case 1: Exit ?
            isExit(raw, 0) -> Success("exit", Codes.EXIT)

            // Case 2a: version ?
            isVersion(raw, 0) -> Success("version", Codes.VERSION)

            // Case 2b: about ?
            ArgsCheck.isAbout(raw, 0) -> Success("about", Codes.ABOUT)

            // Case 3a: Help ?
            ArgsCheck.isHelp(raw, 0) -> Success("help", Codes.HELP)

            else -> Failure(Err.of("other"))
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
        return LogLevel.parse(level)
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

    fun context(args: Args, envs: Envs, about: About, schema: ArgsSchema, enc: Encryptor?, logs: Logs?, confSource:Alias = Alias.Jar): Notice<AppContext> {
        val inputs = inputs(args, envs, about, schema, enc, logs, confSource)
        return inputs.flatMap { Success(buildContext(it, enc, logs)) }
    }

    private fun inputs(args: Args, envs: Envs, about: About, schema: ArgsSchema, enc: Encryptor?, logs: Logs?, confSource:Alias = Alias.Jar): Notice<AppInputs> {
        // We need to determine where the "env.conf" is loaded from.
        // The location is defaulted to load from jars but can be explicitly supplied in args
        // or specified in the "conf.dirs" config setting in the env.conf file
        // 1. user dir: conf.dir=user:/app1  -> ~/app1
        // 2. curr dir: conf.dir=curr:/app1  -> ./app1
        // 3. path dir: conf.dir=path:/app1  -> /app1/
        // 4. temp dir: conf.dir=temp:/app1  -> $TMPDIR/app1
        // 5. conf dir: conf.dir=conf:/app1  -> ./conf
        // 6. jars dir: conf.dir=jars:/app1  -> app.jar/resources
        val source = getDir(args, confSource)
        val envRootName = ConfFuncs.CONFIG_DEFAULT_PROPERTIES
        val props = Props.loadFrom(source.combine(envRootName))
        val confBase = Config(source, props, enc)

        // 2. The environment can be selected in the following order:
        // - command line ( via "-env=dev"   )
        // - env.conf ( via env.name = dev )
        // getEnv will first look for selected environment from args, then in config.
        val envSelected = getEnv(args, confBase)

        // 2. Validate the environment
        // Get all
        val allEnvs = envs.all
        val envCheck = Envs(allEnvs).validate(envSelected)
        val envName = envSelected.name

        return envCheck?.let { env ->
            // 4. We now have the environment to use ( e.g. "dev" )
            // Now load the final environment specific override
            // for directory reference provide: "file://./conf/"
            val overrideConfName = "env.${env.name}" + CONFIG_DEFAULT_SUFFIX
            val overrideConfPath = source.combine(overrideConfName).toFile().absolutePath
            val confEnv = Config.of(overrideConfPath, confBase, enc)

            Success(AppInputs(args, Envs(allEnvs).select(env.name), confBase, confEnv))
        } ?: Failure("Unknown environment name : $envName supplied")
    }

    private fun buildContext(appInputs: AppInputs, enc: Encryptor?, logs: Logs?): AppContext {

        val buildInfoExists = resourceExists("build.conf")
        val build = if (buildInfoExists) {
            val source = getDir(appInputs.args, Alias.Jar).combine("build.conf")
            val props = Props.loadFrom(source)
            val stamp = Config(source,props, enc)
            val info = stamp.buildStamp("build")
            info
        } else {
            Build.empty
        }

        val args = appInputs.args
        val env = appInputs.envs

        // The config is inheritance based.
        // Which means the base env.loc.conf inherits from env.conf.
        val conf = appInputs.confEnv

        return AppContext(
                args = args,
                envs = env,
                conf = conf,
                enc = enc,
                logs = logs ?: LogsDefault,
                about = AppBuilder.about(conf),
                sys = Sys.build(),
                build = build,
                dirs = AppBuilder.folders(conf)
        )
    }

    fun resourceExists(path: String): Boolean {
        val res = this.javaClass.getResource("/$path")
        return res != null
    }

    data class AppInputs(
        val args: Args,
        val envs: Envs,
        val confBase: Conf,
        val confEnv: Conf
    )
}
