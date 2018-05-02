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
import slatekit.common.db.DbLookup
import slatekit.common.db.DbLookup.DbLookupCompanion.defaultDb
import slatekit.common.encrypt.Encryptor
import slatekit.common.envs.*
import slatekit.common.info.*
import slatekit.common.log.*
import slatekit.common.results.ResultFuncs.exit
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.help
import slatekit.common.results.ResultFuncs.success
import slatekit.common.templates.Subs
import slatekit.common.templates.TemplatePart
import slatekit.core.common.AppContext

object AppFuncs {

    /**
     * The list of available environments to choose from.
     * An environment definition is defined by its name, mode
     * The key is built up from name and mode as {name}.{mode}
     * e.g. "qa1.QA"
     *
     * Each of these environments should map to an associated env.{name}.conf
     * config file in the /resources/ directory.
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
    fun envs(): List<Env> =
        listOf(
            Env("loc", Dev , desc = "Dev environment (local)"),
            Env("dev", Dev , desc = "Dev environment (shared)"),
            Env("qa1", Qa  , desc = "QA environment  (current release)"),
            Env("qa2", Qa  , desc = "QA environment  (last release)"),
            Env("stg", Uat , desc = "STG environment (demo)"),
            Env("pro", Prod, desc = "LIVE environment")
        )


    /**
     * Builds the DbLookup containing the database connections :
     * 1. default connection
     * 2. named connections
     * 3. grouped connections
     *
     * @return
     */
    fun dbs(conf: ConfigBase): DbLookup = defaultDb(conf.dbCon("db"))


    /**
     * builds all the info for this application including its
     * id, name, company, contact info, etc.
     *
     * These can be overriden in the config
     *
     * @return
     */
    fun about(conf: ConfigBase): About =
            // Get info about app from base config "env.conf" which is common to all environments.
            About(
                    id = conf.getStringOrElse("app.id", "sampleapp.console"),
                    name = conf.getStringOrElse("app.name", "Sample App - Console"),
                    desc = conf.getStringOrElse("app.desc", "Sample to show the base application"),
                    company = conf.getStringOrElse("app.company", "slatekit"),
                    region = conf.getStringOrElse("app.region", "ny"),
                    version = conf.getStringOrElse("app.version", "0.9.1"),
                    url = conf.getStringOrElse("app.url", "http://sampleapp.slatekit.com"),
                    group = conf.getStringOrElse("app.group", "products-dept"),
                    contact = conf.getStringOrElse("app.contact", "kishore@abc.co"),
                    tags = conf.getStringOrElse("app.tags", "slate,shell,cli"),
                    examples = conf.getStringOrElse("app.examples", "")
            )


    /**
     * builds a list of directories used by the application for logs/output ( NOT BINARIES ).
     * Folders represent the names/locations of the directories
     * used by this application.
     * The structure is a parent/child one based on company/apps/app
     * e.g.
     * - Company
     *    - apps
     *        - app 1
     *            - logs
     *            - cache
     *            - output
     *        - app 2
     *
     * @return
     */
    fun folders(conf: ConfigBase): Folders {

        val abt = about(conf)

        // The root directory can be overriden in the config
        // e..g app.dir = user://company/dept/app
        return Folders.userDir(
                root = conf.getStringOrElse("app.dir", abt.company.toId()),
                group = abt.group.toId(),
                app = abt.id
        )
    }


    /**
     * builds a list of substitutions ( variables ) that can be used dynamically
     * throughout the application to refer to various parts/settings of the app.
     * e.g. used in the .conf files to load settings from a file where the name
     * of the file can be based off the name of the application, company name, etc.
     *
     * @return
     */
    fun vars(conf: ConfigBase): Subs {
        val abt = about(conf)
        return Subs(listOf<Pair<String, (TemplatePart) -> String>>(
                Pair("user.home"   , { _ -> System.getProperty("user.home") }),
                Pair("company.id"  , { _ -> abt.company.toId() }),
                Pair("company.name", { _ -> abt.company }),
                Pair("company.dir" , { _ -> "@{user.home}/@{company.id}" }),
                Pair("root.dir"    , { _ -> "@{company.dir}" }),
                Pair("group.id"    , { _ -> abt.group.toId() }),
                Pair("group.name"  , { _ -> abt.group }),
                Pair("group.dir"   , { _ -> "@{root.dir}/@{group.id}" }),
                Pair("app.id"      , { _ -> abt.id }),
                Pair("app.name"    , { _ -> abt.name }),
                Pair("app.dir"     , { _ -> "@{root.dir}/@{group.id}/@{app.id}" })
        ))
    }


    fun getConfPath(args: Args, file: String, conf: ConfigBase?): String {
        val pathFromArgs = args.getStringOrElse("conf.dir", "")
        val location = pathFromArgs ?: conf?.getStringOrElse("conf.dir", "") ?: ""
        val prefix = when (location) {
            "jars" -> ""
            "conf" -> "file://./conf/"
            ""     -> ""
            else   -> location
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
    fun isMetaCommand(raw: List<String>): ResultMsg<String> {

        // Case 1: Exit ?
        return if (isExit(raw, 0)) {
            exit()
        }
        // Case 2a: version ?
        else if (isVersion(raw, 0)) {
            help()
        }
        // Case 2b: about ?
        // Case 3a: Help ?
        else if (ArgsFuncs.isAbout(raw, 0) || ArgsFuncs.isHelp(raw, 0)) {
            help()
        }
        else {
            failure()
        }
    }


    /**
     * gets the selected environment by key "env" from command line args first or env.conf second
     *
     * @return
     */
    fun getEnv(args: Args, conf: ConfigBase): Env {
        val env = getConfOverride(args, conf, "env", "loc")
        return Env.parse(env)
    }


    /**
     * gets log level by key "log.level" from command line args first or environment config 2nd
     *
     * @return
     */
    fun getLogLevel(args: Args, conf: ConfigBase): LogLevel {
        val level = getConfOverride(args, conf, "log.level", "info")
        return LogHelper.parseLevel(level)
    }


    /**
     * gets log name by key "log.name" from command line args first or environment config 2nd
     *
     * @return
     */
    fun getLogName(args: Args, conf: ConfigBase): String {
        val log = getConfOverride(args, conf, "log.name", "@{app}-@{env}-@{date}.log")
        return log
    }


    fun getConfOverride(args: Args, conf: ConfigBase, key: String, defaultValue: String?): String {

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


    fun buildAppInputs(args: Args, enc: Encryptor?): ResultMsg<AppInputs> {
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
        val allEnvs = envs()
        val envCheck = Envs(allEnvs, allEnvs.firstOrNull()).validate(envSelected)
        val envName = envSelected.name

        return envCheck?.let { env ->
            // 4. We now have the environment to use ( e.g. "dev" )
            // Now load the final environment specific override
            // for directory reference provide: "file://./conf/"
            val overrideConfPath = getConfPath(args, "env.${env.name}" + CONFIG_DEFAULT_SUFFIX, confBase)
            val confEnv = ConfigMulti(overrideConfPath, confBase, enc)

            success(AppInputs(args, envCheck, confBase, confEnv))
        } ?: failure<AppInputs>(msg = "Unknown environment name : $envName supplied")
    }


    fun buildContext(appInputs: AppInputs, enc: Encryptor?, logs: Logs?): AppContext {

        val buildInfoExists = resourceExists("build.conf")
        val build = if (buildInfoExists) {
            val stamp = Config(getConfPath(appInputs.args, "build.conf", null), enc)
            val info = stamp.buildStamp("build")
            info
        }
        else {
            Build.empty
        }

        // The config is inheritance based.
        // Which means the base env.loc.conf inherits from env.conf.
        val conf = ConfigMulti(
                appInputs.confEnv,
                appInputs.confBase,
                enc)

        return AppContext(
                arg = appInputs.args,
                env = appInputs.env,
                cfg = conf,
                enc = enc,
                logs = logs ?: LogsDefault,
                dbs = dbs(conf),
                inf = about(conf).copy(version = build.version),
                host = Host.local(),
                lang = Lang.kotlin(),
                dirs = folders(conf),
                state = Success(true),
                build = build
                //ent = Entities(dbs(conf))
        )
    }


    fun resourceExists(path:String):Boolean {
        val res = this.javaClass.getResource("/$path")
        return res != null
    }
}
