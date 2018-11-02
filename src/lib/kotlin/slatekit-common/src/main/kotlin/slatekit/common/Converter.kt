package slatekit.common

import slatekit.common.records.Record


interface SqlConverter<T> {
    fun toSql(value: T?): String
    fun toItem(record: Record, name: String): T?
}

