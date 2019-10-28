package slatekit.app

import slatekit.common.args.ArgsSchema
import slatekit.common.conf.Conf
import slatekit.common.db.DbLookup
import slatekit.common.info.About
import slatekit.common.info.Folders
import slatekit.common.templates.Subs
import slatekit.common.toId

/**
 * Builds default application components:
 * 1. env   : environments
 * 2. about : info about app
 * 3. schema: command line argument schema
 */
object AppBuilder {

    /**
     * Builds the DbLookup containing the database connections :
     * 1. default connection
     * 2. named connections
     * 3. grouped connections
     *
     * @return
     */
    fun dbs(conf: Conf): DbLookup = DbLookup.defaultDb(conf.dbCon("db"))

    /**
     * builds all the info for this application including its
     * id, name, company, contact info, etc.
     *
     * These can be overriden in the config
     *
     * @return
     */
    fun about(conf: Conf): About = About(
            area = conf.getStringOrElse("app.area", "products-dept"),
            name = conf.getStringOrElse("app.name", "app name"),
            desc = conf.getStringOrElse("app.desc", "app desc"),
            company = conf.getStringOrElse("app.company", "company"),
            region = conf.getStringOrElse("app.region", "ny"),
            version = conf.getStringOrElse("app.version", "1.0.0"),
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
        return Folders.userDir(
                root = conf.getStringOrElse("app.dir", abt.company.toId()),
                area = abt.area.toId(),
                app = abt.name
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
}
