package slatekit.core.app

import slatekit.common.conf.Conf
import slatekit.common.db.DbLookup
import slatekit.common.envs.Env
import slatekit.common.envs.EnvMode
import slatekit.common.info.About
import slatekit.common.info.Folders
import slatekit.common.templates.Subs
import slatekit.common.toId

object AppBuilder {
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
                    Env("loc", EnvMode.Dev, desc = "Dev environment (local)"),
                    Env("dev", EnvMode.Dev, desc = "Dev environment (shared)"),
                    Env("qa1", EnvMode.Qat, desc = "QA environment  (current release)"),
                    Env("qa2", EnvMode.Qat, desc = "QA environment  (last release)"),
                    Env("stg", EnvMode.Uat, desc = "STG environment (demo)"),
                    Env("pro", EnvMode.Pro, desc = "LIVE environment")
            )


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
    fun about(conf: Conf): About =
    // Get info about app from base config "env.conf" which is common to all environments.
            About(
                    id = conf.getStringOrElse("app.id", "app id"),
                    name = conf.getStringOrElse("app.name", "app name"),
                    desc = conf.getStringOrElse("app.desc", "app desc"),
                    company = conf.getStringOrElse("app.company", "company"),
                    region = conf.getStringOrElse("app.region", "ny"),
                    version = conf.getStringOrElse("app.version", "0.9.1"),
                    url = conf.getStringOrElse("app.url", "https://www.slatekit.com"),
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
    fun folders(conf: Conf): Folders {

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
    fun vars(conf: Conf): Subs {
        val abt = about(conf)
        return Subs(listOf(
                Pair("user.home", { _ -> System.getProperty("user.home") }),
                Pair("company.id", { _ -> abt.company.toId() }),
                Pair("company.name", { _ -> abt.company }),
                Pair("company.dir", { _ -> "@{user.home}/@{company.id}" }),
                Pair("root.dir", { _ -> "@{company.dir}" }),
                Pair("group.id", { _ -> abt.group.toId() }),
                Pair("group.name", { _ -> abt.group }),
                Pair("group.dir", { _ -> "@{root.dir}/@{group.id}" }),
                Pair("app.id", { _ -> abt.id }),
                Pair("app.name", { _ -> abt.name }),
                Pair("app.dir", { _ -> "@{root.dir}/@{group.id}/@{app.id}" })
        ))
    }
}