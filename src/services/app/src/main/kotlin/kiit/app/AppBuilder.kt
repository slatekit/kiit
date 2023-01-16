package kiit.app

import kiit.common.args.Args
import kiit.common.args.ArgsSchema
import kiit.common.conf.Conf
import kiit.common.conf.Confs
import kiit.common.conf.Config
import kiit.common.conf.Props
import kiit.common.crypto.Encryptor
import kiit.common.data.Connections
import kiit.common.info.About
import kiit.common.info.Folders
import kiit.utils.templates.Subs
import kiit.common.ext.toId
import kiit.common.info.Build
import kiit.common.info.Info
import kiit.common.io.Alias
import kiit.common.io.Uri
import kiit.common.io.Uris
import kiit.common.log.Logs
import kiit.common.log.LogsDefault
import kiit.context.AppContext
import kiit.results.builders.Tries
import kiit.results.getOrElse
import java.util.*

/**
 * Builds various application components:
 * 1. env   : environments
 * 2. about : info about app
 * 3. schema: command line argument schema
 */
object AppBuilder {

    /**
     * Builds the Connections containing the database connections :
     * 1. default connection
     * 2. named connections
     * 3. grouped connections
     *
     * @return
     */
    fun dbs(conf: Conf): Connections = Connections.of(conf.dbCon("db"))

    /**
     * builds all the info for this application including its
     * id, name, company, contact info, etc.
     *
     * These can be overriden in the config
     *
     * @return
     */
    fun about(conf: Conf): About = About(
        company = conf.getStringOrElse("app.company", "company"),
        area = conf.getStringOrElse("app.area", "products-dept"),
        name = conf.getStringOrElse("app.name", "app name"),
        desc = conf.getStringOrElse("app.desc", "app desc"),
        region = conf.getStringOrElse("app.region", "ny"),
        url = conf.getStringOrElse("app.url", "https://www.slatekit.com"),
        contact = conf.getStringOrElse("app.contact", "kishore@abc.co"),
        tags = conf.getStringOrElse("app.tags", "slate,shell,cli"),
        examples = conf.getStringOrElse("app.examples", "")
    )

    /**
     * setup the command line arguments.
     * NOTE:
     * 1. These values can can be setup in the env.conf file
     * 2. If supplied on command line, they override the values in .conf file
     * 3. If any of these are required and not supplied, then an error is display and program exits
     * 4. Help text can be easily built from this schema.
     */
    fun schema(): ArgsSchema = ArgsSchema()
            .text("", "env", "the environment to run in", false, "dev", "dev", "dev1|qa1|stg1|pro")
            .text("", "config.loc", "location of config files", false, "jar", "jar", "jar|conf")
            .text("", "log.level", "the log level for logging", false, "info", "info", "debug|info|warn|error")

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
    fun folders(conf: Conf): Folders {
        val abt = about(conf)
        // The root directory can be overriden in the config
        // e..g app.dir = user://company/dept/app
        return Folders.userDir(abt)
    }

    /**
     * builds a list of substitutions ( variables ) that can be used dynamically
     * throughout the application to refer to various parts/settings of the app.
     * e.g. used in the .conf files to load settings from a file where the name
     * of the file can be based off the name of the application, company name, etc.
     *
     * @return
     */
    fun vars(abt: About): Subs {
        return Subs(listOf(
            Pair("user.home", { _ -> System.getProperty("user.home") }),
            Pair("company.id", { _ -> abt.company.toId() }),
            Pair("company.name", { _ -> abt.company }),
            Pair("company.dir", { _ -> "@{user.home}/@{company.id}" }),
            Pair("root.dir", { _ -> "@{company.dir}" }),
            Pair("area.id", { _ -> abt.area.toId() }),
            Pair("area.name", { _ -> abt.area }),
            Pair("area.dir", { _ -> "@{root.dir}/@{area.id}" }),
            Pair("app.id", { _ -> abt.id }),
            Pair("app.name", { _ -> abt.name }),
            Pair("app.dir", { _ -> "@{root.dir}/@{area.id}/@{app.id}" })
        ))
    }

    /**
     * Builds the build info file
     */
    fun build(cls:Class<*>, args: Args, alias: Alias = Alias.Jar): Build {
        val result = Tries.of {
            val source = dir(args, alias)
            val name = "build.conf"
            val props = Props.fromUri(cls, source.combine(name))
            val stamp = Config(cls, source, props, null)
            val build = stamp.buildStamp("build")
            build
        }
        return result.getOrElse { Build.empty }
    }


    /**
     * Gets the build info file
     */
    fun build(cls:Class<*>, loc:Uri): Build {
        val result = Tries.of {
            val uri = loc.combine("build.conf")
            val props = Props.fromUri(cls, uri)
            val stamp = Config(cls, uri, props, null)
            val build = stamp.buildStamp("build")
            build
        }
        return result.getOrElse { Build.empty }
    }

    /**
     * Gets the uri from where to load config settings
     */
    fun dir(args: Args, default: Alias): Uri {
        val dirFromArgs = args.getStringOrNull("conf.dir")
        return dirFromArgs?.let { Uris.parse(it) } ?: Uri.of(default, "", null)
    }

    /**
     * Builds the Application Context with all relevant dependencies:
     * 1. args
     * 2. envs
     * 3. confs
     * 4. logs ( defaults )
     * 5. dirs ( defaults )
     */
    fun context(cls:Class<*>, inputs: AppUtils.AppInputs, enc: Encryptor?, logs: Logs?): AppContext {
        val build = build(cls, inputs.loc)
        val args = inputs.args
        val env = inputs.envs

        // The config is inheritance based.
        // Which means the base env.loc.conf inherits from env.conf.
        val conf = inputs.confEnv

        return AppContext(
            app = cls,
            args = args,
            envs = env,
            conf = conf,
            enc = enc,
            logs = logs ?: LogsDefault,
            info = Info.of(about(conf), build),
            dirs = folders(conf)
        )
    }

    fun conf(cls:Class<*>, raw: Array<String>, name:String = Confs.CONFIG_DEFAULT_PROPERTIES, alias:Alias = Alias.Jar): Properties {
        val args = Args.parseArgs(raw).getOrNull() ?: Args.empty()
        return conf(cls, args, name, alias)
    }

    fun conf(cls:Class<*>, args: Args, name:String = Confs.CONFIG_DEFAULT_PROPERTIES, alias:Alias = Alias.Jar): Properties {
        val source = dir(args, alias)
        val path = source.combine(name)
        val props = Props.fromUri(cls, path)
        return props
    }
}
