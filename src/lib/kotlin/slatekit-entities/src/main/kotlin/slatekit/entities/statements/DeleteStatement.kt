package slatekit.entities.statements

import slatekit.common.data.Value
import slatekit.common.data.Values
import slatekit.entities.EntityMapper
import slatekit.entities.core.EntityInfo

open class DeleteStatement<TId, T>(val info: EntityInfo, val mapper: EntityMapper<TId, T>) : Statement<TId, T> where TId : kotlin.Comparable<TId>, T : Any {
    /**
     * Builds the full SQL statement
     * e.g. "delete from movies where id = 1;"
     */
    open fun stmt(id: TId): String {
        val name = encode(info.idInfo.name(), info.encodedChar)
        return "${prefix()} where $name = $id;"
    }

    /**
     * Builds sql statement with values as placeholders for prepared statements
     * e.g.
     * "delete from `movies` where id = ?;"
     */
    open fun prep(id: TId): StatementData {
        val name = encode(info.idInfo.name(), info.encodedChar)
        val sql = "${prefix()} where $name = ?;"
        return StatementData(sql, listOf(Value(name, id)), listOf(id))
    }

    /**
     * Builds sql statement to remove multiple items by ids
     * e.g.
     * "delete from `movies` where id in (?);"
     */
    open fun prep(ids:List<TId>): StatementData {
        val name = encode(info.idInfo.name(), info.encodedChar)
        val sql = "${prefix()} where $name in (?);"
        val delimited = ids.joinToString(",")
        return StatementData(sql, listOf(Value(name, delimited)), listOf(delimited))
    }

    /**
     * Builds the values to be inserted as a list of Pair(name:String, value:Any?)
     * e.g. listOf(
     *      Value("id"    , 1")
     * )
     */
    open fun data(id: TId): Values {
        val name = encode(info.idInfo.name(), info.encodedChar)
        return listOf<Value>(Value(name, id))
    }

    /**
     * Truncate the table to delete all records
     */
    open fun drop(): String = "${prefix()};"

    /**
     * basic syntax for common to both stmt/prep
     */
    fun prefix(): String = "delete from " + encode(info.tableName, info.encodedChar)
}
