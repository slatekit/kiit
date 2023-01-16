/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github 
 *  </kiit_header>
 */

package kiit.common.conf

import kiit.common.io.Files
import kiit.common.*
import kiit.common.data.DbCon
import kiit.common.data.DbConString
import kiit.common.crypto.Encryptor
import kiit.common.info.ApiKey
import kiit.common.info.ApiLogin
import kiit.common.info.Credentials
import java.io.File
import java.util.*

/**
 * Created by kishorereddy on 6/4/17.
 */
object Confs {

    const val CONFIG_DEFAULT_SUFFIX = ".conf"
    const val CONFIG_DEFAULT_PROPERTIES = "env.conf"
    const val CONFIG_DEFAULT_SECTION_DB = "db"
    const val CONFIG_DEFAULT_SECTION_LOGIN = "login"
    const val CONFIG_DEFAULT_SECTION_API = "api"


    /**
     * loads the db info from the location specified
     *
     * @param fileName : the name of the file e.g "db-local.conf"
     * @param enc : the encryptor for decrypting info from the settings
     * @param sectionName : the name of the section in the file representing the settings
     * @return
     */
    fun readDbCon(cls: Class<*>,
                  fileName: String? = null,
                  enc: Encryptor? = null,
                  sectionName: String? = CONFIG_DEFAULT_SECTION_DB
    ): DbCon? =
            load(cls, fileName, enc).dbCon(sectionName ?: CONFIG_DEFAULT_SECTION_DB)

    /**
     * loads the login info from the location specified
     *
     * @param fileName : the name of the file e.g "login.conf"
     * @param enc : the encryptor for decrypting info from the settings
     * @param sectionName : the name of the section in the file representing the settings
     * @return
     */
    fun readLogin(
            cls: Class<*>,
            fileName: String? = null,
            enc: Encryptor? = null,
            sectionName: String? = CONFIG_DEFAULT_SECTION_LOGIN
    ): Credentials? =
            load(cls, fileName, enc).login(sectionName ?: CONFIG_DEFAULT_SECTION_LOGIN)

    /**
     * loads the api key info from the location specified
     *
     * @param fileName : the name of the file e.g "aws.conf"
     * @param sectionName : the name of the section in the file representing the settings
     * @param enc : the encryptor for decrypting info from the settings
     * @return
     */
    fun readApiKey(
            cls: Class<*>,
            fileName: String? = null,
            enc: Encryptor? = null,
            sectionName: String? = CONFIG_DEFAULT_SECTION_API
    ): ApiLogin? =
            load(cls, fileName, enc).apiLogin(sectionName ?: CONFIG_DEFAULT_SECTION_API)

    /**
     * Loads a config file using the source/location supplied.
     *
     * @param fileName : name of file e.g. email.conf ( defaults to "env.conf" )
     * @param enc : the encryptor for decrypting config settings.
     * @return
     */
    fun load(cls: Class<*>, fileName: String? = null, enc: Encryptor? = null): Conf {
        val info = Props.fromPath(cls, fileName)
        return Config(cls, info.first, info.second, enc)
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
                            key("pswd", con.pswd, enc)
                    )
                }
            }

    /**
     * loads the config with primary and the parent
     *
     * @return
     */
    fun loadWithFallbackConfig(cls: Class<*>,
                               fileName: String,
                               parentFilePath: String,
                               enc: Encryptor? = null
    ): Conf {

        val conf = Config.of(cls, fileName, parentFilePath, enc)
        return conf
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
