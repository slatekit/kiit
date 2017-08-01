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
import slatekit.apis.support.ApiWithSupport
import slatekit.common.ApiKey
import slatekit.common.ApiLogin
import slatekit.common.Credentials
import slatekit.common.conf.ConfFuncs
import slatekit.common.db.DbConString
import slatekit.common.db.DbTypeMySql
import slatekit.core.common.AppContext


@Api(area = "sys", name = "config", desc = "api info about the application and host", roles = "*", auth = "key-roles", verb = "post", protocol = "cli")
class ConfigApi(override val context: slatekit.core.common.AppContext) : ApiWithSupport {

    @ApiAction(desc = "creates an api key in the directory", roles = "*", verb = "@parent", protocol = "@parent")
    fun createApiKey(rootDir: String, name: String, key: String, roles: String): slatekit.common.ApiKey {
        val apiKey = ApiKey(name, key, roles)
        slatekit.common.conf.ConfFuncs.createApiKey(rootDir, name, apiKey, context.enc)
        return apiKey
    }


    @ApiAction(desc = "creates an api login in the directory", roles = "*", verb = "@parent", protocol = "@parent")
    fun createApiLogin(rootDir: String, name: String, account: String, key: String, pass: String, env: String, tag: String): slatekit.common.ApiLogin {
        val login = ApiLogin(account, key, pass, env, tag)
        ConfFuncs.createApiLogin(rootDir, name, login, context.enc)
        return login
    }


    @ApiAction(desc = "creates db login in the directory", roles = "*", verb = "@parent", protocol = "@parent")
    fun createDbConMySql(rootDir: String, name: String, url: String, user: String, pass: String): slatekit.common.db.DbConString {
        val dbCon = DbConString(DbTypeMySql.driver, url, user, pass)
        slatekit.common.conf.ConfFuncs.createDbCon(rootDir, name, dbCon, context.enc)
        return dbCon
    }


    @ApiAction(desc = "creates a credentials file in the directory", roles = "*", verb = "@parent", protocol = "@parent")
    fun createCredentials(rootDir: String, name: String, id: String, username: String, email: String,
                          key: String, env: String, region: String): Credentials {
        val credentials = Credentials(id, name, email, key, env, region)
        ConfFuncs.createLogin(rootDir, name, credentials, context.enc)
        return credentials
    }
}
