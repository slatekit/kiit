/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.migrations

import kiit.common.*
import kiit.common.data.DbCon
import kiit.common.data.Connections
import kiit.common.ext.toStringNumeric
import kiit.common.info.Folders
import kiit.common.io.Files
import kiit.common.utils.Props
import kiit.entities.Entities
import kiit.entities.core.EntityContext
import kiit.results.Notice
import kiit.results.Success
import kiit.results.Try
import kiit.results.builders.Tries
import kiit.results.getOrElse

/**
 * Created by kreddy on 3/23/2016.
 */
class MigrationService(
        private val entities: Entities,
        private val dbs: Connections?,
        private val settings: MigrationSettings,
        private val folders: Folders?
) {

    fun names(): List<Pair<String, String>> = entities.getEntities().map {
        Pair(it.entityTypeName, it.model.table )
    }


    fun counts(): List<Pair<String, Long>> = entities.getEntities().map {
        Pair(it.entityTypeName, it.entityRepoInstance.count())
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
            is kiit.results.Success -> {
                val db = entities.builder.db(dbKey, dbShard)
                result.value.forEach {
                    if (!it.isNullOrEmpty()) { db.update(it) }
                }
                kiit.results.Success("Installed all tables")
            }
            is kiit.results.Failure -> {
                kiit.results.Failure(result.error, msg = err)
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
        return operate("Delete", name) { info -> builder(info.entityTypeName).clear(info.model) }
    }

    fun drop(name: String): Try<String> {
        return operate("Drop", name) { info -> builder(info.entityTypeName).remove(info.model) }
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
        val results = entities.getEntities().map { entity ->
            val result = generateSql(entity.entityTypeName)
            result.map {
                val allSqlForModel = it.joinToString(newline)
                allSqlForModel
            }
        }
        val succeeded = results.filter { it.success }
        val allSql = succeeded.fold("") { acc, result ->
            acc + newline + "-- ${result.desc}" + newline + result.getOrElse { "Error generating sql" }
        }
        val finalFileName = "$fileName.sql"
        val filePath = folders?.let {
            Files.writeDatedFile(folders.pathToOutputs, finalFileName, allSql)
            val filePath = folders.pathToOutputs + Props.pathSeparator + finalFileName
            filePath
        } ?: "Folders not available, sql files not written"
        return kiit.results.Success(filePath)
    }

    fun generateSqlAllUninstall(): Try<String> {
        val fileName = "sql-all-uninstall-" + DateTime.now().toStringNumeric()
        val results = entities.getEntities().map { entity ->
            val builder = builder(entity.entityTypeName)
            val dropTable = builder.remove(entity.model)
            Success(dropTable, msg = "Dropping table for model : " + entity.model.name)
        }
        val succeeded = results.filter { it.success }
        val allSql = succeeded.fold("") { acc, result ->
            acc + newline + "-- ${result.desc}" + newline + result.getOrElse { "Error generating sql" }
        }
        val finalFileName = "$fileName.sql"
        val filePath = folders?.let {
            Files.writeDatedFile(folders.pathToOutputs, finalFileName, allSql)
            val filePath = folders.pathToOutputs + Props.pathSeparator + finalFileName
            filePath
        } ?: "Folders not available, sql files not written"
        return kiit.results.Success(filePath)
    }

    fun deleteAll(): Try<List<String>> {
        return each { entity -> delete(entity.entityTypeName) }
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
            val info = entities.getInfoByName(moduleName)
            val ddl = builder(fullName)
            val sqlTable = ddl.create(info.model)
            val sqlIndexes = ddl.createIndexes(info.model)
            val sql: List<String> = listOf(sqlTable).plus(sqlIndexes)
            val filePath = if (settings.enableOutput) {
                folders?.let { folders ->
                    val fileName = "model-${info.model.name}.sql"
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
        return if (success) kiit.results.Success(sql, msg = info) else kiit.results.Failure(Exception(info), msg = info)
    }

    fun connection(): Notice<DbCon> {
        return dbs?.let { dbs ->
            kiit.results.Success(dbs.default() ?: DbCon.empty)
        } ?: kiit.results.Failure("no db setup")
    }

    fun connectionByName(name: String): Notice<DbCon> {
        return dbs?.let { dbs ->
            kiit.results.Success(dbs.named(name) ?: DbCon.empty)
        } ?: kiit.results.Failure("no db setup")
    }

    private fun operate(operationName: String, entityName: String, sqlBuilder: (EntityContext) -> String): Try<String> {
        val ent = entities.getInfoByName(entityName)
        val svc = entities.getServiceByTypeName(entityName)
        val model = entities.getModel(entityName)
        val table = model.table
        val sql = sqlBuilder(ent)
        return try {
            val db = entities.getDb()
            db.update(sql)
            kiit.results.Success("Operation $operationName successful on $table")
        } catch (ex: Exception) {
            kiit.results.Failure(ex, msg = "Unable to delete :$table. ${ex.message}")
        }
    }

    private fun each(operation: (EntityContext) -> Try<String>): Try<List<String>> {
        val results = entities.getEntities().map { operation(it ) }
        val success = results.all { it.success }
        val messages = results.map { it.desc ?: "" }
        val error = if (success) "" else messages.joinToString(newline)
        return if (success) kiit.results.Success(messages, msg = "") else kiit.results.Failure(Exception(error), msg = error)
    }

    private fun builder(name:String):SqlBuilder {
        val info = entities.getInfoByName(name)
        return SqlBuilderDDL(info.entityRepoInstance.dialect, null)
    }
}
