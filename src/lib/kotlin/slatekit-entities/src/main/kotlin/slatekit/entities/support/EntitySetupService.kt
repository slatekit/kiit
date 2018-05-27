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

import slatekit.common.*
import slatekit.common.db.DbCon
import slatekit.common.db.DbConEmpty
import slatekit.common.db.DbLookup
import slatekit.common.info.Folders
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.success
import slatekit.entities.core.Entities
import slatekit.entities.core.EntityInfo
import slatekit.meta.buildAddTable

/**
 * Created by kreddy on 3/23/2016.
 */
class EntitySetupService(private val _entities: Entities,
                         private val _dbs: DbLookup?,
                         private val _settings: EntitySetupSettings,
                         private val _folders: Folders?) {

    fun names(): List<Pair<String, String>> = _entities.getEntities().map {
        Pair(it.entityTypeName, it.entityRepoInstance?.repoName() ?: it.entityTypeName )
    }


    fun counts(): List<Pair<String, Long>> = _entities.getEntities().map {
        Pair(it.entityTypeName, it.entityRepoInstance?.count() ?: 0 )
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
    fun install(name: String, version: String = "", dbKey: String = "", dbShard: String = ""): ResultEx<String> {
        val result = generateSql(name, version)
        val err = "Unable to install, can not generate sql for model $name"

       return  when(result) {
            is Success -> {
                val db = _entities.getDb(dbKey, dbShard)
                db.update(result.data)
                Success("Installed all tables")
            }
            is Failure ->  {
                Failure(result.err, msg = err)
            }
        }
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
    fun uinstall(name: String): ResultEx<String> {
        delete(name)
        return drop(name)
    }


    fun delete(name:String): ResultEx<String> {
        return operate("Delete", name, { info, tableName -> _entities.getDbSource().buildDeleteAll(tableName) } )
    }


    fun drop(name:String): ResultEx<String> {
        return operate("Drop", name, { info, tableName -> _entities.getDbSource().buildDropTable(tableName) } )
    }


    fun installAll(): ResultEx<List<String>> {
        return each( { entity -> install(entity.entityTypeName) } )
    }


    fun generateSqlAll(): ResultEx<List<String>> {
        return each( { entity -> generateSql(entity.entityTypeName) } )
    }


    fun deleteAll(): ResultEx<List<String>> {
        return each( { entity -> delete(entity.entityTypeName) } )
    }


    fun dropAll(): ResultEx<List<String>> {
        return each( { entity -> drop(entity.entityTypeName) } )
    }


    /**
     * generates the sql for installing the model, file is created in the .{appname}/apps/ directory.
     *
     * @param name    : the fully qualified name of the model e..g slate.ext.resources.Resource
     * @param version : the version of the model
     * @return
     */
    fun generateSql(name: String, version: String = ""): ResultEx<String> {
        val result = try {
            val fullName = name
            val svc = _entities.getServiceByName(fullName)
            val model = svc.repo().mapper().model()
            val sql = buildAddTable(_entities.getDbSource(), model, namer = _entities.namer)
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
        return if(success) Success(sql, msg = info) else Failure(Exception(info), msg = info)
    }


    fun connectionByDefault(): ResultMsg<DbCon> {
        return _dbs?.let { dbs ->
            success(dbs.default() ?: DbConEmpty)
        } ?: failure<DbCon>("no db setup")
    }


    fun connectionByName(name: String): ResultMsg<DbCon> {
        return _dbs?.let { dbs ->
            success(dbs.named(name) ?: DbConEmpty)
        } ?: failure<DbCon>("no db setup")
    }


    private fun operate(operationName:String, entityName:String, sqlBuilder: (EntityInfo, String) -> String): ResultEx<String> {
        val ent = _entities.getInfoByName(entityName)
        val svc = _entities.getServiceByName(entityName)
        val table = svc.repo().repoName()
        val sql = sqlBuilder(ent, table)
        return try {
            val db = _entities.getDb()
            db.update(sql)
            Success("Operation $operationName successful on $table")
        } catch ( ex: Exception ) {
            Failure(ex, msg="Unable to delete :$table. ${ex.message}")
        }
    }


    private fun each(operation: (EntityInfo) -> ResultEx<String>): ResultEx<List<String>> {
        val results =  _entities.getEntities().map { operation(it) }
        val success = results.all { it.success }
        val messages = results.map { it.msg ?: "" }
        val error = if(success) "" else messages.joinToString(newline)
        return if(success) Success(messages, msg = "") else Failure(Exception(error), msg = error)
    }
}
