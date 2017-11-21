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
import slatekit.apis.support.ApiBase
import slatekit.common.Result
import slatekit.common.db.DbCon
import slatekit.entities.support.EntitySetupService
import slatekit.entities.support.EntitySetupSettings
import slatekit.integration.common.AppEntContext


@Api(area = "infra", name = "entities", desc = "api to access and manage data models",
        roles = "admin", auth = "key-roles", verb = "post", protocol = ApiConstants.ProtocolCLI)
class EntitiesApi(context: AppEntContext) : ApiBase(context) {
    val appContext = context

    @ApiAction(desc = "installs the model to the database shard", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun install(name: String, version: String = "", dbKey: String = "", dbShard: String = ""): Result<Boolean> {
        return service().install(name, version, dbKey, dbShard)
    }


    @ApiAction(desc = "installs all the models in the default database", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun installAll(): Result<String> {
        return service().installAll()
    }


    @ApiAction(desc = "installs all the models in the default database", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun names(): List<String> {
        return service().names()
    }


    @ApiAction(desc = "generates sql install files for the model", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun generateSql(name: String, version: String = ""): Result<String> {
        return service().generateSql(name, version)
    }


    @ApiAction(desc = "generates sql install files for all models", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun generateSqlAll(): Result<String> {
        return service().generateSqlAll()
    }


    @ApiAction(desc = "gets the default db connection", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun connectionByDefault(): Result<DbCon> {
        return service().connectionByDefault()
    }


    @ApiAction(desc = "gets the default db connection", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun connectionByName(name: String): Result<DbCon> {
        return service().connectionByName(name)
    }


    private fun service(): EntitySetupService {
        return EntitySetupService(appContext.ent, context.dbs, EntitySetupSettings(), context.dirs)
    }
}
