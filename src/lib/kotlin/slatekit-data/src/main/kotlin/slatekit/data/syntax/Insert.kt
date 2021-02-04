package slatekit.data.syntax

import slatekit.common.data.*
import slatekit.data.Consts
import slatekit.data.core.Meta

/**
 * Used to build the insert statements
 * @param info: Meta info to know about the table (name, primary key ) and model id
 * @param mapper: Mapper that converts a model T into its values for a table
 */
open class Insert<TId, T>(val info: Meta<TId, T>, val mapper: Mapper<TId, T>)
    : Statement<TId, T> where TId : kotlin.Comparable<TId>, T : Any {

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
        val start = "insert into " + encode(info.name, info.table.encodeChar)
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
