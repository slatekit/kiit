package slatekit.data.syntax

import slatekit.common.data.*
import slatekit.data.core.Meta

/**
 * Used to build the syntax for select statements
 * @param info: Meta info to know about the table (name, primary key ) and model id
 * @param mapper: Mapper that converts a model T into its values for a table
 * @param filters: Used to build conditions
 */
open class Select<TId, T>(val info: Meta<TId, T>, val mapper: Mapper<TId, T>, val filters: Filters) : Statement<TId, T> where TId : kotlin.Comparable<TId>, T : Any {
    /**
     * Builds the full SQL statement
     * e.g. "select * from `movies` where id = 1;"
     */
    open fun stmt(id: TId): String {
        val name = encode(info.pkey.name, info.table.encodeChar)
        return "${prefix()} where $name = $id;"
    }

    /**
     * Builds the full SQL statement
     * e.g. "select * from `movies` where id = 1;"
     */
    open fun stmt(ids:List<TId>): String {
        val name = encode(info.pkey.name, info.table.encodeChar)
        val delimited = ids.joinToString(",")
        val sql = "${prefix()} where $name in ($delimited);"
        return sql
    }

    /**
     * Builds sql statement with values as placeholders for prepared statements
     * e.g. "select * from `movies` where id = ?;"
     */
    open fun prep(id: TId): Command {
        val name = encode(info.pkey.name, info.table.encodeChar)
        val sql = "${prefix()} where $name = ?;"
        return Command(sql, listOf(Value(name, id)), listOf(id))
    }

    /**
     * Builds the values to be inserted as a list of Pair(name:String, value:Any?)
     * e.g. listOf(
     *      Value("id", 2)
     * )
     */
    open fun data(id: TId): Values {
        val name = encode(info.pkey.name, info.table.encodeChar)
        return listOf<Value>(Value(name, id))
    }

    /**
     * Load all records
     */
    open fun load(): String = "${prefix()};"

    /**
     * basic syntax for common to both stmt/prep
     */
    fun prefix(): String = "select * from " + encode(info.name, info.table.encodeChar)

    /**
     * basic syntax getting count
     */
    fun count(): String = "select count(*) from " + encode(info.name, info.table.encodeChar)

    /**
     * builds a select based on filters
     */
    fun filter(filters: List<Filter>): Command {
        val prefix = prefix()
        val values = filters.map { Encoding.convertVal(it.value) }
        val op = "and"
        val conditions = filters.joinToString(" $op ", transform = { f ->
            this.filters.build(f.name, f.op, f.value, surround = true, placehoder = true)
        })
        val sql = "$prefix where ${conditions};"
        return Command(sql, emptyValues, values)
    }

    /**
     * Takes the first or last N items
     */
    fun take(count:Int, desc:Boolean): String {
        val orderBy = if (desc) " order by id desc" else " order by id asc"
        return "${prefix()} $orderBy limit $count;"
    }

    private val emptyValues:List<Value> = listOf()
}
