package slatekit.common

import slatekit.common.records.Record


interface SqlConverter<T> {
    fun toSql(item: Any, name: String): Any?
    fun toItem(record: Record, name: String): T?
}

