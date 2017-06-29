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

package slatekit.entities.support

import slatekit.common.Files
import slatekit.common.Props
import slatekit.common.Result
import slatekit.common.db.DbCon
import slatekit.common.db.DbConEmpty
import slatekit.common.db.DbLookup
import slatekit.common.info.Folders
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.ok
import slatekit.common.results.ResultFuncs.success
import slatekit.common.results.ResultFuncs.successOrError
import slatekit.entities.core.Entities
import slatekit.entities.core.EntityInfo


/**
 * Created by kreddy on 3/23/2016.
 */
class EntitySetupService(val _entities: Entities,
                         val _dbs: DbLookup?,
                         val _settings: EntitySetupSettings,
                         val _folders: Folders?) {

    fun names(): List<String> = _entities.getEntities().map { it.entityTypeName }


    /**
     * generates all the sql install files for all the registered entities
     *
     * @return
     */
    fun eachEntity(callback: (EntityInfo) -> Unit): Unit {
        _entities.getEntities().forEach(callback)
    }


    fun <T> consolidate(results: List<Result<*>>): Result<String> {

        val success = results.all { result -> result.success }
        val msg = if (success) {
            results.fold("", { acc, r -> acc + (r.value ?: "") })
        }
        else {
            results[0].msg
        }
        return successOrError(success, msg)
    }


    /**
     * installs all the registered entities in the database
     *
     * @return
     */
    fun installAll(): Result<String> {
        val results = _entities.getEntities().map { entity ->
            install(entity.entityTypeName, "1", entity.dbKey, entity.dbShard)
        }
        return consolidate<String>(results)
    }


    /**
     * generates all the sql install files for all the registered entities
     *
     * @return
     */
    fun generateSqlAll(): Result<String> {
        val results = _entities.getEntities().map { entity ->
            generateSql(entity.entityTypeName, "1")
        }
        return consolidate<Boolean>(results)
    }


    /**
     * installs the model name supplied into the database.
     *
     * @param name    : the fully qualified name of the model e..g slate.ext.resources.Resource
     * @param version : the version of the model
     * @param dbKey   : the dbKey pointing to the database to install the model to. leave empty to use default db
     * @param dbShard : the dbShard pointing to the database to install the model to. leave empty to use default db
     * @return
     */
    fun install(name: String, version: String = "", dbKey: String = "", dbShard: String = ""): Result<Boolean> {
        val result = generateSql(name, version)
        val err = "Unable to install, can not generate sql for model $name"

        return result.value?.let { sql ->
            val db = _entities.getDb(dbKey, dbShard)
            db.update(sql)
            ok()
        } ?: failure(msg = err)
    }


    /**
     * generates the sql for installing the model, file is created in the .{appname}/apps/ directory.
     *
     * @param name    : the fully qualified name of the model e..g slate.ext.resources.Resource
     * @param version : the version of the model
     * @return
     */
    fun generateSql(name: String, version: String = ""): Result<String> {
        val result = try {
            val fullName = name
            val svc = _entities.getServiceByName(fullName)
            val model = svc.repo().mapper().model()
            val sql = _entities.getDbSource().builAddTable(model)
            val filePath = if (_settings.enableOutput) {
                _folders?.let { folders ->
                    val fileName = "model-${model.name}.sql"
                    Files.writeDatedFile(folders.pathToOutputs, fileName, sql)
                    folders.pathToOutputs + Props.pathSeparator + fileName
                }
            }
            else {
                ""
            }
            Triple(true, filePath, sql)
        }
        catch(ex: Exception) {
            Triple(false, ex.message, "")
        }

        val success = result.first
        val sql = result.third
        val path = result.second

        val info = if (success) "generated sql for model: $name $path" else "error generating sql"
        return successOrError(success, sql, info)
    }


    fun connectionByDefault(): Result<DbCon> {
        return _dbs?.let { dbs ->
            success(dbs.default() ?: DbConEmpty)
        } ?: failure<DbCon>("no db setup")
    }


    fun connectionByName(name: String): Result<DbCon> {
        return _dbs?.let { dbs ->
            success(dbs.named(name) ?: DbConEmpty)
        } ?: failure<DbCon>("no db setup")
    }
}
