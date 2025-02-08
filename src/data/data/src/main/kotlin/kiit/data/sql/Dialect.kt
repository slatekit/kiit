package kiit.data.sql

import kiit.data.core.Table
import kiit.data.syntax.Types
import kiit.query.Op

/**
 * Contains syntax builders to build Sql and/or Prepared Sql/statements
 */
open class Dialect(val types: Types = Types(),
                   val aggr: Aggregates = Aggregates(),
                   val encodeChar:Char = '`',
                   val useSchema:Boolean = false
    ) {
    /**
     * Encodes the name using the encode char e.g. column1 = `column1`
     */
    fun encode(name:String): String = "${encodeChar}${name}${encodeChar}"

    fun encode(schema:String, name:String): String {
        return when(useSchema) {
            true  -> "${encodeChar}${name}${encodeChar}"
            false -> "${encodeChar}${schema}${encodeChar}.${encodeChar}${name}${encodeChar}"
        }
    }


    open fun op(op: Op): String = op.text
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


    fun select(table:Table): kiit.query.Select
    fun patch (table:Table): kiit.query.Update
    fun delete(table:Table): kiit.query.Delete
}


