package kiit.migrations


import kiit.data.sql.Dialect
import slatekit.meta.models.Model

/**
 * DDL building is very basic, just deal w/ the table creation only.
 * All other future migrations are based on SQL files.
 */
interface SqlBuilder {
    val dialect: Dialect

    /**
     * Builds the table DDL sql statement using the model supplied.
     */
    fun create(model: Model): String


    fun remove(model: Model): String


    fun clear(model: Model): String

    /**
     * Creates primary key DDL
     */
    fun createPrimaryKey(name: String): String

    /**
     * Builds the table DDL sql statement using the model supplied.
     */
    fun createColumns(prefix: String?, model: Model, filterId: Boolean): String

    /**
     * Create index on column
     */
    fun createIndexes(model: Model): List<String>
}


