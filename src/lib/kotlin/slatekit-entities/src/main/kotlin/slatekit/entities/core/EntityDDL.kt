package slatekit.entities.core

import slatekit.common.naming.Namer
import slatekit.common.db.Db
import slatekit.common.db.types.DbSource
import slatekit.meta.models.Model


interface EntityDDL {

    /**
     * creates a table in the database that matches the schema(fields) in the model supplied
     *
     * @param model : The model associated with the table.
     */
    fun createTable(db: Db, model: Model)


    fun buildIndexes(db:Db, model:Model, namer: Namer?):List<String>


    /**
     * Builds the table DDL sql statement using the model supplied.
     */
    fun buildAddTable(dbSrc: DbSource, model: Model, namer: Namer? = null): String


    fun buildPrimaryKey(name: String): String


    fun buildCreateTable(name: String): String
 }
