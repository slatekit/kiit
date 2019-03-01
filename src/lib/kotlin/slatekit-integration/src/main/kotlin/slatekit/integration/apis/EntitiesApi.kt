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
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.apis.support.ApiBase
import slatekit.common.db.DbCon
import slatekit.common.newline
import slatekit.common.db.DbLookup
import slatekit.integration.common.AppEntContext
import slatekit.orm.core.OrmEntities
import slatekit.orm.migrations.OrmMigrationService
import slatekit.orm.migrations.OrmMigrationSettings
import slatekit.results.Notice
import slatekit.results.Try

@Api(area = "infra", name = "entities", desc = "api to access and manage data models",
        auth = AuthModes.apiKey, roles = "admin", verb = Verbs.auto, protocol = Protocols.all)
class EntitiesApi(context: AppEntContext) : ApiBase(context) {
    val appContext = context

    @ApiAction(desc = "installs the model to the database shard")
    fun install(name: String, version: String = "", dbKey: String = "", dbShard: String = ""): Try<String> {
        return service().install(name, version, dbKey, dbShard)
    }

    @ApiAction(desc = "installs all the models in the default database")
    fun installAll(): Try<List<String>> {
        return service().installAll()
    }

    @ApiAction(desc = "deletes data in the table supplied")
    fun delete(name: String): Try<String> {
        return service().delete(name)
    }

    @ApiAction(desc = "deletes data in all the tables")
    fun deleteAll(): Try<List<String>> {
        return service().deleteAll()
    }

    @ApiAction(desc = "drops the table supplied")
    fun drop(name: String): Try<String> {
        return service().drop(name)
    }

    @ApiAction(desc = "dropss the table")
    fun dropAll(): Try<List<String>> {
        return service().dropAll()
    }

    @ApiAction(desc = "installs all the models in the default database")
    fun names(): List<Pair<String, String>> {
        return service().names()
    }

    @ApiAction(desc = "installs all the models in the default database")
    fun counts(): List<Pair<String, Long>> {
        //return service().counts()
        return listOf()
    }

    @ApiAction(desc = "generates sql install files for the model")
    fun generateSql(name: String, version: String = ""): Try<String> {
        return service().generateSql(name, version).map { it.joinToString(newline) }
    }

    @ApiAction(desc = "generates sql install files for all models")
    fun generateSqlFiles(): Try<List<String>> {
        return service().generateSqlFiles()
    }

    @ApiAction(desc = "generates a single sql install file for all models")
    fun generateSqlAllInstall(): Try<String> {
        return service().generateSqlAllInstall()
    }

    @ApiAction(desc = "generates a single sql install file for all models")
    fun generateSqlAllUninstall(): Try<String> {
        return service().generateSqlAllUninstall()
    }

    @ApiAction(desc = "gets the default db connection")
    fun connection(): Notice<DbCon> {
        return service().connection()
    }

    @ApiAction(desc = "gets the default db connection")
    fun connectionByName(name: String): Notice<DbCon> {
        return service().connectionByName(name)
    }

    private val dbLookup by lazy { DbLookup.fromConfig(context.cfg) }

    private fun service(): OrmMigrationService {
        return OrmMigrationService(appContext.ent as OrmEntities, dbLookup, OrmMigrationSettings(), context.dirs)
    }
}
