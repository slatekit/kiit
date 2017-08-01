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
import slatekit.apis.ApiBase
import slatekit.common.Result
import slatekit.common.db.DbCon
import slatekit.core.common.AppContext
import slatekit.entities.core.Entities
import slatekit.entities.support.EntitySetupService
import slatekit.entities.support.EntitySetupSettings


@slatekit.apis.Api(area = "infra", name = "entities", desc = "api to access and manage data models",
        roles = "admin", auth = "key-roles", verb = "post", protocol = "cli")
class EntitiesApi(context: slatekit.core.common.AppContext) : slatekit.apis.ApiBase(context) {
    val appContext = context

    @ApiAction(desc = "installs the model to the database shard", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun install(name: String, version: String = "", dbKey: String = "", dbShard: String = ""): slatekit.common.Result<Boolean> {
        return service().install(name, version, dbKey, dbShard)
    }


    @ApiAction(desc = "installs all the models in the default database", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun installAll(): slatekit.common.Result<String> {
        return service().installAll()
    }


    @ApiAction(desc = "installs all the models in the default database", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun names(): List<String> {
        return service().names()
    }


    @ApiAction(desc = "generates sql install files for the model", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun generateSql(name: String, version: String = ""): slatekit.common.Result<String> {
        return service().generateSql(name, version)
    }


    @ApiAction(desc = "generates sql install files for all models", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun generateSqlAll(): slatekit.common.Result<String> {
        return service().generateSqlAll()
    }


    @ApiAction(desc = "gets the default db connection", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun connectionByDefault(): slatekit.common.Result<DbCon> {
        return service().connectionByDefault()
    }


    @ApiAction(desc = "gets the default db connection", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun connectionByName(name: String): slatekit.common.Result<DbCon> {
        return service().connectionByName(name)
    }


    private fun service(): slatekit.entities.support.EntitySetupService {
        return slatekit.entities.support.EntitySetupService(appContext.ent as Entities, context.dbs, EntitySetupSettings(), context.dirs)
    }
}
