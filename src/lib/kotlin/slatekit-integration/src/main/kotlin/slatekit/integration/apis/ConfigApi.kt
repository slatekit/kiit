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
import slatekit.apis.ApiAction
import slatekit.apis.ApiConstants
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.apis.support.ApiWithSupport
import slatekit.common.ApiKey
import slatekit.common.ApiLogin
import slatekit.common.Credentials
import slatekit.common.conf.ConfFuncs
import slatekit.common.conf.Config
import slatekit.common.db.DbCon
import slatekit.common.db.DbConString
import slatekit.common.db.DbTypeMySql


@Api(area = "infra", name = "configs", desc = "api info about the application and host",
        auth = AuthModes.apiKey, roles = "admin", verb = Verbs.auto, protocol = Protocols.cli)
class ConfigApi(override val context: slatekit.core.common.AppContext) : ApiWithSupport {

    @ApiAction(desc = "creates an api key in the directory")
    fun createApiKey(rootDir: String, name: String, key: String, roles: String): slatekit.common.ApiKey {
        val apiKey = ApiKey(name, key, roles)
        slatekit.common.conf.ConfFuncs.createApiKey(rootDir, name, apiKey, context.enc)
        return apiKey
    }


    @ApiAction(desc = "creates an api login in the directory")
    fun createApiLogin(rootDir: String, name: String, account: String, key: String, pass: String, env: String, tag: String): slatekit.common.ApiLogin {
        val login = ApiLogin(account, key, pass, env, tag)
        ConfFuncs.createApiLogin(rootDir, name, login, context.enc)
        return login
    }


    @ApiAction(desc = "creates db login in the directory")
    fun createDbConMySql(rootDir: String, name: String, url: String, user: String, pass: String): slatekit.common.db.DbConString {
        val dbCon = DbConString(DbTypeMySql.driver, url, user, pass)
        slatekit.common.conf.ConfFuncs.createDbCon(rootDir, name, dbCon, context.enc)
        return dbCon
    }


    @ApiAction(desc = "creates a credentials file in the directory")
    fun createCredentials(rootDir: String, name: String, id: String, username: String, email: String,
                          key: String, env: String, region: String): Credentials {
        val credentials = Credentials(id, name, email, key, env, region)
        ConfFuncs.createLogin(rootDir, name, credentials, context.enc)
        return credentials
    }


    @ApiAction(desc = "loads and shows the database info from config")
    fun showDbDefault(): DbCon? {
        return context.cfg.dbCon()
    }


    @ApiAction(desc = "loads and shows the database info from config with supplied name")
    fun showDbNamed(name:String): DbCon? {
        return context.cfg.dbCon(name)
    }


    @ApiAction(desc = "loads and shows the database info from config with supplied name")
    fun showDbFromUri(path:String, name:String): DbCon? {
        val conf = Config(path)
        val dbCon = conf.dbCon(name)
        return dbCon
    }


    @ApiAction(desc = "loads and shows an api login info from config")
    fun showApiLogin(name:String): ApiLogin {
        return context.cfg.apiLogin(name)
    }


    @ApiAction(desc = "loads and shows an api key from config")
    fun showApiLoginFromUri(path:String, name:String): ApiLogin {
        val conf = Config(path)
        val apiInfo = conf.apiLogin(name)
        return apiInfo
    }


    @ApiAction(desc = "loads and shows an api key from config")
    fun showApiKey(name:String): ApiLogin {
        return context.cfg.apiLogin(name)
    }


    @ApiAction(desc = "loads and shows an api key from config")
    fun showApiKeyFromUri(path:String, name:String): ApiLogin {
        val conf = Config(path)
        val apiInfo = conf.apiLogin(name)
        return apiInfo
    }
}
