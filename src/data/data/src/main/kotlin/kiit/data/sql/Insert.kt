package kiit.data.sql

import kiit.common.data.*
import kiit.data.Consts
import kiit.data.core.Meta
import kiit.data.Mapper

/**
 * Used to build the insert statements for a model using a mapper
 * @param meta: Meta info to know about the table (name, primary key ) and model id
 * @param mapper: Mapper that converts a model T into its values for a table
 */
open class Insert<TId, T>(val dialect: Dialect,
                          val meta: Meta<TId, T>,
                          val mapper: Mapper<TId, T>) where TId : kotlin.Comparable<TId>, T : Any {

    /**
     * Builds the command for an insert
     * e.g.
     * sql = "insert into `movies` ( name, category ) values ( 'batman', 'action' );"
     * prep = "insert into `movies` ( name, category ) values ( ?, ? );",
     *          listOf(
     *              Value("name", "batman"),
     *              Value("category", "action")
     *          )
     */
    open fun build(item:T, mode: BuildMode = BuildMode.Prep): Command {

        val start = "insert into " + dialect.encode(meta.table.schema, meta.table.name)
        val values = mapper.encode(item, DataAction.Create, null)
        val cols = "(" + values.joinToString(",", transform = { it.name }) + ") "
        return when(mode){
            BuildMode.Sql -> {
                val args = "VALUES (" + values.joinToString(",", transform = { it.text ?: Consts.NULL }) + ")"
                val sql = "$start $cols $args;"
                Command(sql, values, values.map { it.value })
            }
            BuildMode.Prep -> {
                val args= "VALUES (" + values.joinToString(",", transform = { "?" }) + ") "
                val sql = "$start $cols $args;"
                Command(sql, values, values.map { it.value })
            }
        }
    }
}
