package slatekit.entities.statements

import slatekit.common.data.DataAction
import slatekit.common.data.Values
import slatekit.entities.Consts
import slatekit.entities.EntityMapper
import slatekit.entities.core.EntityInfo

open class InsertStatement<TId, T>(val info: EntityInfo, val mapper: EntityMapper<TId, T>) : Statement<TId, T> where TId : kotlin.Comparable<TId>, T : Any {
    /**
     * Builds the full SQL statement
     * e.g. "insert into `movies` ( name, category ) values ( 'batman', 'action' );"
     */
    open fun stmt(item: T): String {
        val values = data(item)
        val cols = "(" + values.joinToString(",", transform = { encode(it.name, info.encodedChar) }) + ") "
        val args = "VALUES (" + values.joinToString(",", transform = { it.value?.toString() ?: Consts.NULL }) + ")"
        return "${prefix()} $cols $args;"
    }

    /**
     * Builds sql statement with values as placeholders for prepared statements
     * e.g.
     * "insert into `movies` ( name, category ) values ( ?, ? );"
     */
    open fun prep(item: T): StatementData {
        val values = data(item)
        val cols = "(" + values.joinToString(",", transform = { encode(it.name, info.encodedChar) }) + ") "
        val args = "VALUES (" + values.joinToString(",", transform = { "?" }) + ") "
        val sql = "${prefix()} $cols $args;"
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
        return mapper.encode(item, DataAction.Create, null)
    }


    /**
     * basic syntax for common to both stmt/prep
     */
    private fun prefix(): String = "insert into " + encode(info.tableName, info.encodedChar)
}
