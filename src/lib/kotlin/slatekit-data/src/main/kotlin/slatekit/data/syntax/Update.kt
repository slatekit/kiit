package slatekit.data.syntax

import slatekit.common.data.*
import slatekit.data.Consts
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
     * basic syntax for common to both stmt/prep
     */
    fun prefix(): String = "update " + encode(info.name, info.table.encodeChar)


    private val emptyValues:List<Value> = listOf()
}
