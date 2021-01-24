package slatekit.data.syntax

import slatekit.common.data.*
import slatekit.data.Consts
import slatekit.common.data.Filter
import slatekit.data.core.Meta

/**
 * Used to build the syntax for update statements
 * @param info: Meta info to know about the table (name, primary key ) and model id
 * @param mapper: Mapper that converts a model T into its values for a table
 */
open class Update<TId, T>(val info: Meta<TId, T>, val mapper: Mapper<TId, T>, val filters:Filters = Filters()) : Statement<TId, T> where TId : kotlin.Comparable<TId>, T : Any {
    /**
     * Builds the full SQL statement
     * e.g. "update `movies` set name = 'batman', category = 'action';"
     */
    open fun stmt(item: T): String {
        val values = data(item)
        val args = values.joinToString(",", transform = { "${it.name} = ${it.value?.toString() ?: Consts.NULL}" } )
        return "${prefix()} SET $args;"
    }

    /**
     * Builds sql statement with values as placeholders for prepared statements
     * e.g.
     * "update `movies` ( name, category ) values ( ?, ? );"
     */
    open fun prep(item: T): Command {
        val values = data(item)
        val args = values.joinToString(",", transform = { "${it.name} = ?" } )
        val sql = "${prefix()} SET $args;"
        return Command(sql, values, values.map { it.value })
    }

    /**
     * Builds the values to be inserted as a list of Pair(name:String, value:Any?)
     * e.g. listOf(
     *      Value("name"    , "batman"),
     *      Value("category", "action")
     * )
     */
    open fun data(item: T): Values {
        return mapper.encode(item, DataAction.Update, null)
    }

    /**
     * builds a select based on filters
     */
    open fun patch(updates:List<Value>, filters: List<Filter>, logical:Logical): Command {
        val prefix = prefix()
        // set category = 1
        val changesValues = updates.map { Encoding.convertVal(it.value) }

        // set category = ?, activated = ?
        val changes = updates.joinToString(",", transform = { c ->
            this.filters.update(c.name, c.value, true, true)
        })

        // where category = ?, activated = ?
        val op = if(logical == Logical.And) "and" else "or"
        val conditionValues = filters.map { Encoding.convertVal(it.value) }
        val conditions = filters.joinToString(" and ", transform = { f ->
            this.filters.build(f.name, f.op, f.value, surround = true, placehoder = true)
        })
        val sql = "$prefix set ${changes}, where ${conditions};"
        val values = changesValues + conditionValues
        return Command(sql, emptyValues, values)
    }

    /**
     * basic syntax for common to both stmt/prep
     */
    fun prefix(): String = "update " + encode(info.name, info.table.encodeChar)


    private val emptyValues:List<Value> = listOf()
}
