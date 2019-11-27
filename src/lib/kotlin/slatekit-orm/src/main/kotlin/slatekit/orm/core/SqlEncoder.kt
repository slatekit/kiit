package slatekit.orm.core

import slatekit.common.Record


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

