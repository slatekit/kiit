package slatekit.data.encoders

import slatekit.common.Record
import slatekit.common.data.Value

/**
 * Interface to support encoding to/from kotlin value to a SQL value
 */

interface SqlEncoder<T> {

    /**
     * Encodes the value as a string
     */
    fun encode(value: T?): String

    /**
     * Converts the value to a @see[slatekit.common.data.Value]
     * which can be more easily automapped to database type
     */
    fun convert(name:String, value:T?): Value

    /**
     * Decodes the value from the record
     */
    fun decode(record: Record, name: String): T?
}

