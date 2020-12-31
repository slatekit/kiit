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

package slatekit.integration.apis

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.AuthModes
import slatekit.apis.Verbs
import slatekit.apis.support.FileSupport
import slatekit.common.Sources
import slatekit.common.info.ApiKey
import slatekit.common.info.ApiLogin
import slatekit.common.info.Credentials
import slatekit.common.conf.Confs
import slatekit.common.conf.Config
import slatekit.common.data.DbCon
import slatekit.common.data.DbConString
import slatekit.common.data.Vendor.MySql
import slatekit.common.crypto.Encryptor
import slatekit.common.log.Logger
import slatekit.context.Context

@Api(area = "infra", name = "configs", desc = "api info about the application and host",
        auth = AuthModes.KEYED, roles = ["admin"], verb = Verbs.AUTO, sources = [Sources.CLI])
class ConfigApi(override val context: Context) : FileSupport {

    override val encryptor: Encryptor? = context.enc
    override val logger: Logger? = context.logs.getLogger()

    @Action(desc = "creates an api key in the directory")
    fun createApiKey(rootDir: String, name: String, key: String, roles: String): ApiKey {
        val apiKey = ApiKey(name, key, roles)
        slatekit.common.conf.Confs.createApiKey(rootDir, name, apiKey, context.enc)
        return apiKey
    }

    @Action(desc = "creates an api login in the directory")
    fun createApiLogin(rootDir: String, name: String, account: String, key: String, pass: String, env: String, tag: String): ApiLogin {
        val login = ApiLogin(account, key, pass, env, tag)
        Confs.createApiLogin(rootDir, name, login, context.enc)
        return login
    }

    @Action(desc = "creates db login in the directory")
    fun createDbConMySql(rootDir: String, name: String, url: String, user: String, pass: String): slatekit.common.data.DbConString {
        val dbCon = DbConString(MySql.driver, url, user, pass)
        slatekit.common.conf.Confs.createDbCon(rootDir, name, dbCon, context.enc)
        return dbCon
    }

    @Action(desc = "creates a credentials file in the directory")
    fun createCredentials(
        rootDir: String,
        name: String,
        id: String,
        username: String,
        email: String,
        key: String,
        env: String,
        region: String
    ): Credentials {
        val credentials = Credentials(id, name, email, key, env, region)
        Confs.createLogin(rootDir, name, credentials, context.enc)
        return credentials
    }

    @Action(desc = "loads and shows the database info from config")
    fun showDbDefault(): DbCon? {
        return context.conf.dbCon()
    }

    @Action(desc = "loads and shows the database info from config with supplied name")
    fun showDbNamed(name: String): DbCon? {
        return context.conf.dbCon(name)
    }

    @Action(desc = "loads and shows the database info from config with supplied name")
    fun showDbFromUri(path: String, name: String): DbCon? {
        val conf = Config.of(context.app, path)
        val dbCon = conf.dbCon(name)
        return dbCon
    }

    @Action(desc = "loads and shows an api login info from config")
    fun showApiLogin(name: String): ApiLogin {
        return context.conf.apiLogin(name)
    }

    @Action(desc = "loads and shows an api key from config")
    fun showApiLoginFromUri(path: String, name: String): ApiLogin {
        val conf = Config.of(context.app, path)
        val apiInfo = conf.apiLogin(name)
        return apiInfo
    }

    @Action(desc = "loads and shows an api key from config")
    fun showApiKey(name: String): ApiLogin {
        return context.conf.apiLogin(name)
    }

    @Action(desc = "loads and shows an api key from config")
    fun showApiKeyFromUri(path: String, name: String): ApiLogin {
        val conf = Config.of(context.app, path)
        val apiInfo = conf.apiLogin(name)
        return apiInfo
    }
}
