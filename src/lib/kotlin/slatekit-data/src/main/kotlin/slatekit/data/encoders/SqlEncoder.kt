package slatekit.data.encoders

import slatekit.common.Record

/**
 * Interface to support encoding to/from kotlin value to a SQL value
 */

interface SqlEncoder<T> {

    /**
     * Encodes the value as a string
     */
    fun encode(value: T?): String

    /**
     * Decodes the value from the record
     */
    fun decode(record: Record, name: String): T?
}

