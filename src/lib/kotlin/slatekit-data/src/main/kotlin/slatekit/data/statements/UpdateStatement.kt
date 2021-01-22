package slatekit.data.statements

import slatekit.common.data.DataAction
import slatekit.common.data.Mapper
import slatekit.common.data.Values
import slatekit.data.Consts
import slatekit.data.core.Meta

open class UpdateStatement<TId, T>(val info: Meta<TId, T>, val mapper: Mapper<TId, T>) : Statement<TId, T> where TId : kotlin.Comparable<TId>, T : Any {
    /**
     * Builds the full SQL statement
     * e.g. "update `movies` set name = 'batman', category = 'action';"
     */
    open fun stmt(item: T): String {
        val values = data(item)
        val args = values.joinToString(",", transform = { "${encode(it.name, info.table.encodeChar)} = ${it.value?.toString() ?: Consts.NULL}" } )
        return "${prefix()} SET $args;"
    }

    /**
     * Builds sql statement with values as placeholders for prepared statements
     * e.g.
     * "update `movies` ( name, category ) values ( ?, ? );"
     */
    open fun prep(item: T): StatementData {
        val values = data(item)
        val args = values.joinToString(",", transform = { "${encode(it.name, info.table.encodeChar)} = ?" } )
        val sql = "${prefix()} SET $args;"
        return StatementData(sql, values, values.map { it.value })
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
}
