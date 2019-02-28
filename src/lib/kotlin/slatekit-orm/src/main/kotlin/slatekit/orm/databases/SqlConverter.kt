package slatekit.orm.databases

import slatekit.common.Record


interface SqlConverter<T> {
    fun toSql(value: T?): String
    fun toItem(record: Record, name: String): T?
}

