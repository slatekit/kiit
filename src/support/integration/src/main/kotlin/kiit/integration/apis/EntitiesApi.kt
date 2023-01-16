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

package kiit.integration.apis

import kiit.apis.Api
import kiit.apis.Action
import kiit.apis.AuthModes
import kiit.apis.Verbs
import kiit.apis.ApiBase
import kiit.common.Sources
import kiit.common.data.DbCon
import kiit.common.newline
import kiit.common.data.Connections
import kiit.connectors.entities.AppEntContext
import kiit.migrations.MigrationService
import kiit.migrations.MigrationSettings
import kiit.results.Notice
import kiit.results.Try

@Api(area = "infra", name = "entities", desc = "api to access and manage data models",
        auth = AuthModes.KEYED, roles = ["admin"], verb = Verbs.AUTO, sources = [Sources.ALL])
class EntitiesApi(context: AppEntContext) : ApiBase(context) {
    val appContext = context

    @Action(desc = "installs the model to the database shard")
    fun install(name: String, version: String = "", dbKey: String = "", dbShard: String = ""): Try<String> {
        return service().install(name, version, dbKey, dbShard)
    }

    @Action(desc = "installs all the models in the default database")
    fun installAll(): Try<List<String>> {
        return service().installAll()
    }

    @Action(desc = "deletes data in the table supplied")
    fun delete(name: String): Try<String> {
        return service().delete(name)
    }

    @Action(desc = "deletes data in all the tables")
    fun deleteAll(): Try<List<String>> {
        return service().deleteAll()
    }

    @Action(desc = "drops the table supplied")
    fun drop(name: String): Try<String> {
        return service().drop(name)
    }

    @Action(desc = "dropss the table")
    fun dropAll(): Try<List<String>> {
        return service().dropAll()
    }

    @Action(desc = "installs all the models in the default database")
    fun names(): List<Pair<String, String>> {
        val service = service()
        return service.names()
    }

    @Action(desc = "installs all the models in the default database")
    fun counts(): List<Pair<String, Long>> {
        return service().counts()
    }

    @Action(desc = "generates sql install files for the model")
    fun generateSql(name: String, version: String = ""): Try<String> {
        return service().generateSql(name, version).map { it.joinToString(newline) }
    }

    @Action(desc = "generates sql install files for all models")
    fun generateSqlFiles(): Try<List<String>> {
        return service().generateSqlFiles()
    }

    @Action(desc = "generates a single sql install file for all models")
    fun generateSqlAllInstall(): Try<String> {
        return service().generateSqlAllInstall()
    }

    @Action(desc = "generates a single sql install file for all models")
    fun generateSqlAllUninstall(): Try<String> {
        return service().generateSqlAllUninstall()
    }

    @Action(desc = "gets the default db connection")
    fun connection(): Notice<DbCon> {
        return service().connection()
    }

    @Action(desc = "gets the default db connection")
    fun connectionByName(name: String): Notice<DbCon> {
        return service().connectionByName(name)
    }

    private fun service(): MigrationService {
        return MigrationService(appContext.ent, appContext.dbs, MigrationSettings(), context.dirs)
    }
}
