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

package slatekit.orm.migrations

import slatekit.common.*
import slatekit.common.db.DbCon
import slatekit.common.db.DbLookup
import slatekit.common.ext.toStringNumeric
import slatekit.common.info.Folders
import slatekit.common.io.Files
import slatekit.common.utils.Props
import slatekit.results.Notice
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.getOrElse
import slatekit.orm.core.Entities
import slatekit.orm.core.EntityInfo

/**
 * Created by kreddy on 3/23/2016.
 */
class EntitySetupService(
        private val _entities: Entities,
        private val _dbs: DbLookup?,
        private val _settings: EntitySetupSettings,
        private val _folders: Folders?
) {

    fun names(): List<Pair<String, String>> = _entities.getEntities().map {
        Pair(it.entityTypeName, it.entityRepoInstance?.repoName() ?: it.entityTypeName)
    }

    fun counts(): List<Pair<String, Long>> = _entities.getEntities().map {
        Pair(it.entityTypeName, it.entityRepoInstance?.count() ?: 0)
    }

    /**
     * installs the model name supplied into the database.
     *
     * @param name : the fully qualified name of the model e..g slate.ext.resources.Resource
     * @param version : the version of the model
     * @param dbKey : the dbKey pointing to the database to install the model to. leave empty to use default db
     * @param dbShard : the dbShard pointing to the database to install the model to. leave empty to use default db
     * @return
     */
    fun install(name: String, version: String = "", dbKey: String = "", dbShard: String = ""): Try<String> {
        val result = generateSql(name, version)
        val err = "Unable to install, can not generate sql for model $name"

       return when (result) {
            is slatekit.results.Success -> {
                val db = _entities.builder.db(dbKey, dbShard)
                result.value.forEach {
                    if (!it.isNullOrEmpty()) { db.update(it) }
                }
                slatekit.results.Success("Installed all tables")
            }
            is slatekit.results.Failure -> {
                slatekit.results.Failure(result.error, msg = err)
            }
        }
    }

    /**
     * installs the model name supplied into the database.
     *
     * @param name : the fully qualified name of the model e..g slate.ext.resources.Resource
     * @param version : the version of the model
     * @param dbKey : the dbKey pointing to the database to install the model to. leave empty to use default db
     * @param dbShard : the dbShard pointing to the database to install the model to. leave empty to use default db
     * @return
     */
    fun uinstall(name: String): Try<String> {
        delete(name)
        return drop(name)
    }

    fun delete(name: String): Try<String> {
        return operate("Delete", name, { info, tableName -> _entities.getDbSource().buildDeleteAll(tableName) })
    }

    fun drop(name: String): Try<String> {
        return operate("Drop", name, { info, tableName -> _entities.getDbSource().buildDropTable(tableName) })
    }

    fun installAll(): Try<List<String>> {
        return each({ entity -> install(entity.entityTypeName) })
    }

    fun generateSqlFiles(): Try<List<String>> {
        return each { entity ->
            val result = generateSql(entity.entityTypeName)
            result.map { it.joinToString(newline) }
        }
    }

    fun generateSqlAllInstall(): Try<String> {
        val fileName = "sql-all-install-" + DateTime.now().toStringNumeric()
        val results = _entities.getEntities().map { entity ->
            val result = generateSql(entity.entityTypeName)
            result.map {
                val allSqlForModel = it.joinToString(newline)
                allSqlForModel
            }
        }
        val succeeded = results.filter { it.success }
        val allSql = succeeded.fold("", { acc, result ->
            acc + newline + "-- ${result.msg}" + newline + result.getOrElse { "Error generating sql" }
        })
        val finalFileName = "$fileName.sql"
        Files.writeDatedFile(_folders!!.pathToOutputs, finalFileName, allSql)
        val filePath = _folders!!.pathToOutputs + Props.pathSeparator + finalFileName
        return slatekit.results.Success(filePath)
    }

    fun generateSqlAllUninstall(): Try<String> {
        val fileName = "sql-all-uninstall-" + DateTime.now().toStringNumeric()
        val results = _entities.getEntities().map { entity ->

            val dropTable = _entities.getDbSource().buildDropTable(entity.model.table)

            Success(dropTable, msg = "Dropping table for model : " + entity.model.name)
        }
        val succeeded = results.filter { it.success }
        val allSql = succeeded.fold("", { acc, result ->
            acc + newline + "-- ${result.msg}" + newline + result.getOrElse { "Error generating sql" }
        })
        val finalFileName = "$fileName.sql"
        Files.writeDatedFile(_folders!!.pathToOutputs, finalFileName, allSql)
        val filePath = _folders!!.pathToOutputs + Props.pathSeparator + finalFileName
        return slatekit.results.Success(filePath)
    }

    fun deleteAll(): Try<List<String>> {
        return each({ entity -> delete(entity.entityTypeName) })
    }

    fun dropAll(): Try<List<String>> {
        return each({ entity -> drop(entity.entityTypeName) })
    }

    /**
     * generates the sql for installing the model, file is created in the .{appname}/apps/ directory.
     *
     * @param moduleName : the fully qualified moduleName of the model e..g slate.ext.resources.Resource
     * @param version : the version of the model
     * @return
     */
    fun generateSql(moduleName: String, version: String = ""): Try<List<String>> {
        val result = try {
            val fullName = moduleName
            val svc = _entities.getSvcByTypeName(fullName)
            val model = svc.repo().mapper().model()
            val ddl = _entities.getInfoByName(fullName).entityDDL
            val sqlTable = ddl?.createTable(model) ?: ""
            val sqlIndexes = ddl?.createIndex(model) ?: listOf()
            val sql: List<String> = listOf(sqlTable).plus(sqlIndexes)
            val filePath = if (_settings.enableOutput) {
                _folders?.let { folders ->
                    val fileName = "model-${model.name}.sql"
                    Files.writeDatedFile(folders.pathToOutputs, fileName, sql.joinToString(newline))
                    folders.pathToOutputs + Props.pathSeparator + fileName
                }
            } else {
                ""
            }
            Triple(true, filePath, sql)
        } catch (ex: Exception) {
            Triple(false, ex.message, listOf(""))
        }

        val success = result.first
        val sql = result.third
        val path = result.second
        val info = if (success) "generated sql for model: $moduleName $path" else "error generating sql"
        return if (success) slatekit.results.Success(sql, msg = info) else slatekit.results.Failure(Exception(info), msg = info)
    }

    fun connection(): Notice<DbCon> {
        return _dbs?.let { dbs ->
            slatekit.results.Success(dbs.default() ?: DbCon.empty)
        } ?: slatekit.results.Failure("no db setup")
    }

    fun connectionByName(name: String): Notice<DbCon> {
        return _dbs?.let { dbs ->
            slatekit.results.Success(dbs.named(name) ?: DbCon.empty)
        } ?: slatekit.results.Failure("no db setup")
    }

    private fun operate(operationName: String, entityName: String, sqlBuilder: (EntityInfo, String) -> String): Try<String> {
        val ent = _entities.getInfoByName(entityName)
        val svc = _entities.getSvcByTypeName(entityName)
        val table = svc.repo().repoName()
        val sql = sqlBuilder(ent, table)
        return try {
            val db = _entities.getDb()
            db.update(sql)
            slatekit.results.Success("Operation $operationName successful on $table")
        } catch (ex: Exception) {
            slatekit.results.Failure(ex, msg = "Unable to delete :$table. ${ex.message}")
        }
    }

    private fun each(operation: (EntityInfo) -> Try<String>): Try<List<String>> {
        val results = _entities.getEntities().map { operation(it) }
        val success = results.all { it.success }
        val messages = results.map { it.msg ?: "" }
        val error = if (success) "" else messages.joinToString(newline)
        return if (success) slatekit.results.Success(messages, msg = "") else slatekit.results.Failure(Exception(error), msg = error)
    }
}
