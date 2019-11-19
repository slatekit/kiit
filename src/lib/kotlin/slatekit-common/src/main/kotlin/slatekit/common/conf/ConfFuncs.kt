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

import slatekit.common.io.Files
import slatekit.common.*
import slatekit.common.db.DbCon
import slatekit.common.db.DbConString
import slatekit.common.encrypt.Encryptor
import slatekit.common.info.ApiKey
import slatekit.common.info.ApiLogin
import slatekit.common.info.Credentials
import slatekit.common.io.Uri
import slatekit.common.io.Uris
import java.io.File
import java.util.*

/**
 * Created by kishorereddy on 6/4/17.
 */
object ConfFuncs {


    val CONFIG_DEFAULT_SUFFIX = ".conf"
    val CONFIG_DEFAULT_PROPERTIES = "env.conf"
    val CONFIG_DEFAULT_SECTION_DB = "db"
    val CONFIG_DEFAULT_SECTION_LOGIN = "login"
    val CONFIG_DEFAULT_SECTION_API = "api"


    /**
     * loads the db info from the location specified
     *
     * @param fileName : the name of the file e.g "db-local.conf"
     * @param enc : the encryptor for decrypting info from the settings
     * @param sectionName : the name of the section in the file representing the settings
     * @return
     */
    fun readDbCon(
        fileName: String? = null,
        enc: Encryptor? = null,
        sectionName: String? = CONFIG_DEFAULT_SECTION_DB
    ): DbCon? =
            load(fileName, enc).dbCon(sectionName ?: CONFIG_DEFAULT_SECTION_DB)

    /**
     * loads the login info from the location specified
     *
     * @param fileName : the name of the file e.g "login.conf"
     * @param enc : the encryptor for decrypting info from the settings
     * @param sectionName : the name of the section in the file representing the settings
     * @return
     */
    fun readLogin(
        fileName: String? = null,
        enc: Encryptor? = null,
        sectionName: String? = CONFIG_DEFAULT_SECTION_LOGIN
    ): Credentials? =
            load(fileName, enc).login(sectionName ?: CONFIG_DEFAULT_SECTION_LOGIN)

    /**
     * loads the api key info from the location specified
     *
     * @param fileName : the name of the file e.g "aws.conf"
     * @param sectionName : the name of the section in the file representing the settings
     * @param enc : the encryptor for decrypting info from the settings
     * @return
     */
    fun readApiKey(
        fileName: String? = null,
        enc: Encryptor? = null,
        sectionName: String? = CONFIG_DEFAULT_SECTION_API
    ): ApiLogin? =
            load(fileName, enc).apiLogin(sectionName ?: CONFIG_DEFAULT_SECTION_API)

    /**
     * Loads a config file using the source/location supplied.
     *
     * @param fileName : name of file e.g. email.conf ( defaults to "env.conf" )
     * @param enc : the encryptor for decrypting config settings.
     * @return
     */
    fun load(fileName: String? = null, enc: Encryptor? = null): Conf {
        val info = Props.load(fileName)
        return Config(info.first, info.second, enc)
    }

    /**
     * creates a api credentials file in the app directory of the user home path
     * e.g. {rootDir}/{name}.conf
     *
     * @param appName : The name of the app directory
     * @return
     */
    fun createApiKey(rootDir: String, name: String, creds: ApiKey, enc: Encryptor?): String =
            createFile(rootDir, name + CONFIG_DEFAULT_SUFFIX) {
                createSection(name) {
                    listOf(
                            key("account", creds.key, enc),
                            key("key", creds.name, enc),
                            key("pass", creds.roles, enc)
                    )
                }
            }

    /**
     * creates a api credentials file in the app directory of the user home path
     * e.g. {rootDir}/{name}.conf
     *
     * @param appName : The name of the app directory
     * @return
     */
    fun createApiLogin(rootDir: String, name: String, creds: ApiLogin, enc: Encryptor?): String =
            createFile(rootDir, name + CONFIG_DEFAULT_SUFFIX) {
                createSection(name) {
                    listOf(
                            key("account", creds.account, enc),
                            key("key", creds.key, enc),
                            key("pass", creds.pass, enc),
                            key("env", creds.env, enc),
                            key("tag", creds.tag, enc)
                    )
                }
            }

    /**
     * creates a login file in the app directory of the user home path
     * e.g. {rootDir}/{name}.conf
     *
     * @param appName : The name of the app directory
     * @return
     */
    fun createLogin(rootDir: String, name: String, creds: Credentials, enc: Encryptor?): String =
            createFile(rootDir, name + CONFIG_DEFAULT_SUFFIX) {
                createSection(name) {
                    listOf(
                            key("id", creds.id, enc),
                            key("name", creds.name, enc),
                            key("email", creds.email, enc),
                            key("region", creds.region, enc),
                            key("key", creds.key, enc),
                            key("env", creds.env, enc)
                    )
                }
            }

    fun createDbCon(rootDir: String, name: String, con: DbConString, enc: Encryptor?): String =

            createFile(rootDir, name + CONFIG_DEFAULT_SUFFIX) {
                createSection(name) {
                    listOf(
                            key("driver", con.driver, null),
                            key("url", con.url, enc),
                            key("user", con.user, enc),
                            key("pswd", con.password, enc)
                    )
                }
            }

    /**
     * loads the config with primary and the parent
     *
     * @return
     */
    fun loadWithFallbackConfig(
        fileName: String,
        parentFilePath: String,
        enc: Encryptor? = null
    ): Conf {

        val conf = Config.of(fileName, parentFilePath, enc)
        return conf
    }

    /**
     * Loads the typesafe config from the filename can be prefixed with a uri to indicate location,
     * such as:
     * 1. "jars://" to indicate loading from resources directory inside jar
     * 2. "user://" to indicate loading from user.home directory
     * 3. "file://" to indicate loading from file system
     *
     * e.g.
     *  - jars://env.qa.conf
     *  - user://${company.dir}/${group.dir}/${app.id}/conf/env.qa.conf
     *  - file://c:/slatekit/${company.dir}/${group.dir}/${app.id}/conf/env.qa.conf
     *  - file://./conf/env.qa.conf
     *
     * @param fileName : name of file e.g. email.conf
     * @return
     */
    fun loadPropertiesFrom(fileName: String?): Properties {
        return Props.loadFrom(fileName)
    }

    /**
     * creates a folder inside the app directory of the user home path
     * e.g. {user.home}/{appName}
     *      c:/users/kreddy/myapp/logs
     *
     * @param appName : The name of the app directory
     * @return
     */
    fun createFile(appName: String, name: String, callback: () -> String): String {
        val userHome = System.getProperty("user.home")
        require(!userHome.isNullOrEmpty()) { "Unable to load user directory from 'user.home' system property" }

        Files.mkUserDir(appName)

        // {user}/{app/{file}
        val path = userHome + File.separator + appName + File.separator + name
        val file = File(path)
        val content = callback()
        file.writeText(content)
        return file.absolutePath
    }

    fun createSection(name: String, keys: () -> List<Pair<String, String>>): String {
        val content = keys().fold("", { acc, c -> acc + name + "." + c.first + " = " + c.second + newline }) + newline
        return content
    }

    fun key(name: String, value: String, enc: Encryptor?): Pair<String, String> {
        return Pair(name, enc?.encrypt(value) ?: value)
    }
}
