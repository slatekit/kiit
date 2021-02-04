package slatekit.data.syntax

import slatekit.common.data.*
import slatekit.data.core.Meta

/**
 * Used to build the syntax for select statements
 * @param info: Meta info to know about the table (name, primary key ) and model id
 * @param mapper: Mapper that converts a model T into its values for a table
 * @param filters: Used to build conditions
 */
open class Select<TId, T>(val info: Meta<TId, T>, val mapper: Mapper<TId, T>, val filters: Filters)
    : Statement<TId, T> where TId : kotlin.Comparable<TId>, T : Any {

    /**
     * Builds the full SQL statement
     * e.g. "delete from movies where id = 1;"
     */
    open fun build(id: TId, mode: BuildMode = BuildMode.Prep): Command {
        val start = prefix()
        val column = encode(info.pkey.name, info.table.encodeChar)
        return when(mode) {
            BuildMode.Sql -> {
                val sql = "${prefix()} where $column = $id;"
                Command(sql, emptyValues, emptyValues)
            }
            BuildMode.Prep -> {
                val sql = "$start where $column = ?;"
                Command(sql, listOf(Value(column, info.pkey.type, id, id.toString())), listOf(id))
            }
        }
    }

    /**
     * Builds sql statement to remove multiple items by ids
     * e.g.
     * "delete from `movies` where id in (?);"
     */
    open fun build(ids:List<TId>, mode: BuildMode = BuildMode.Prep): Command {
        val column = encode(info.pkey.name, info.table.encodeChar)
        return when(mode) {
            BuildMode.Sql -> {
                val delimited = ids.joinToString(",")
                val sql = "${prefix()} where $column in ($delimited);"
                Command(sql, emptyValues, emptyValues)
            }
            BuildMode.Prep -> {
                val sql = "${prefix()} where $column = ?;"
                val values = ids.map { Value(column, info.pkey.type, it) }
                Command(sql, values, ids)
            }
        }
    }

    /**
     * Load all records
     */
    open fun all(): Command = Command("${prefix()};", emptyValues, emptyValues)

    /**
     * basic syntax for common to both stmt/prep
     */
    fun prefix(): String = "select * from " + encode(info.name, info.table.encodeChar)

    /**
     * basic syntax getting count
     */
    fun count(): String = "select count(*) from " + encode(info.name, info.table.encodeChar)

    /**
     * Takes the first or last N items
     */
    fun take(count:Int, desc:Boolean): String {
        val orderBy = if (desc) " order by id desc" else " order by id asc"
        return "${prefix()} $orderBy limit $count;"
    }

    private val emptyValues:List<Value> = listOf()
}
