package slatekit.data.sql

import slatekit.data.core.Table
import slatekit.data.slatekit.data.syntax.Types

/**
 * Contains syntax builders to build Sql and/or Prepared Sql/statements
 */
open class Dialect(val types: Types = Types(),
                   val aggr: Aggregates = Aggregates(),
                   val encodeChar:Char = '`') {
    /**
     * Encodes the name using the encode char e.g. column1 = `column1`
     */
    fun encode(name:String): String = "${encodeChar}${name}${encodeChar}"
}


interface Provider<TId, T> where TId: kotlin.Comparable<TId>, T: Any  {

    val dialect: Dialect

    /**
     * Builds insert statement sql/prepared sql
     */
    val insert: Insert<TId, T>

    /**
     * Builds update statement sql/prepared sql
     */
    val update: Update<TId, T>


    fun select(table:Table): slatekit.query.Select
    fun patch (table:Table): slatekit.query.Update
    fun delete(table:Table): slatekit.query.Delete
}


