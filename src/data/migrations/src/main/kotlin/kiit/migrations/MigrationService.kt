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
import kiit.data.sql.vendors.SqlDDLBuilder
import kiit.data.sql.vendors.SqlDDLGroup
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
    fun uninstall(name: String): Try<String> {
        delete(name)
        return drop(name)
    }

    fun delete(name: String): Try<String> {
        return operate("Delete", name) { info -> builder(info.entityTypeName).clear(info.model) }
    }

    fun drop(name: String): Try<String> {
        return operate("Drop", name) { info -> builder(info.entityTypeName).delete(info.model) }
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

    /**
     * Generates SQL DDL files to drop tables.
     * 1. Grouping : Groups the models/tables by schema ( Postgres ).
     * 2. If exists: Generates safe drop table if exists
     * 3. File name: Uses optional file name prefixes
     * @param schemaFilePrefixes : schema name to schema file prefix e.g. "users" -> "delete-01-app-users"
     *
     * @sample
     * 1. File: 01-app-users.sql ( "users" schema )
     *    create table if not exists "users"."account" ...
     *    create table if not exists "users"."device"  ...
     *
     * 2. File: 02-app-content.sql ( "content" schema )
     *    create table if not exists "content"."event" ...
     *    create table if not exists "content"."task"  ...
     */
    fun generateSqlAllInstall(schemaFilePrefixes:List<Pair<String,String>> = listOf()): Try<String> {
        if(folders == null) {
            return Tries.errored("Folders not provided")
        }

        // 1. Get all the entities ( Context which has the Model containing schema names )
        val entities = entities.getEntities()

        // 2. Group all entities by their schema. e.g. "users"
        val schemas = entities.groupBy { it.model.schema }.values.toList()

        // 3. Convert all the models per schema into drop statements.
        schemas.map { items ->
            val ddls = items.map { entity ->
                generateSql(entity.entityTypeName).map { Pair(entity, it) }
            }
            val successes = ddls.filter { it.success }.map { (it as Success).value }
            val tableDDLs = successes.map { it.second.joinToString(newline) }
            val schemaDDL = tableDDLs.joinToString(newline + newline)
            val schema = items.first().model.schema
            val statements = listOf(schemaDDL)
            write(schema, statements, "sql-install", schemaFilePrefixes)
        }
        return kiit.results.Success(folders.pathToOutputs)
    }


    /**
     * Generates SQL DDL files to drop tables.
     * 1. Grouping : Groups the models/tables by schema ( Postgres ).
     * 2. If exists: Generates safe drop table if exists
     * 3. File name: Uses optional file name prefixes
     * @param schemaFilePrefixes : schema name to schema file prefix e.g. "users" -> "delete-01-app-users"
     *
     * @sample
     * 1. File: delete-01-app-users.sql ( "users" schema )
     *    drop table if exists "users"."account";
     *    drop table if exists "users"."device";
     *
     * 2. File: delete-02-app-content.sql ( "content" schema )
     *    drop table if exists "content"."event";
     *    drop table if exists "content"."task";
     */
    fun generateSqlAllUninstall(schemaFilePrefixes:List<Pair<String,String>> = listOf()): Try<String> {
        if(folders == null) {
            return Tries.errored("Folders not provided")
        }

        // 1. Get all the entities ( Context which has the Model containing schema names )
        val entities = entities.getEntities()

        // 2. Group all entities by their schema. e.g. "users"
        val schemas = entities.groupBy { it.model.schema }.values.toList()

        // 3. Convert all the models per schema into drop statements.
        val schemaDDLs = schemas.map { items ->
            val ddl = items.map { entity ->
                val builder = builder(entity.entityTypeName)
                val stmt = builder.delete(entity.model)
                stmt
            }
            SqlDDLGroup(items[0].model.schema, ddl)
        }

        // 4. Generate ddl per schema
        schemaDDLs.forEach { schemaDDL ->

            // 5. Use a prefix name here.
            val schema = schemaDDL.schema
            val statements = schemaDDL.statements
            write(schema, statements, "sql-remove", schemaFilePrefixes)
        }
        return kiit.results.Success(folders.pathToOutputs)
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

    private fun write(schema:String, statements:List<String>, prefixDefault:String,  schemaFilePrefixes:List<Pair<String,String>> = listOf()): String {
        val prefixMatch = schemaFilePrefixes.firstOrNull { it.first.lowercase() == schema.lowercase() }
        val prefix = prefixMatch?.second ?: prefixDefault
        val fileName = "${prefix}-${schema}.sql"
        val allSql = statements.joinToString(newline)
        Files.writeDatedFile(folders!!.pathToOutputs, fileName, allSql)
        val filePath = folders.pathToOutputs + Props.pathSeparator + fileName
        return filePath
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

    private fun builder(name:String):SqlDDLBuilder {
        val info = entities.getInfoByName(name)
        return SqlDDLBuilder(info.entityRepoInstance.dialect, null)
    }
}

