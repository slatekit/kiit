package slatekit.data.sql.vendors

import slatekit.data.Mapper
import slatekit.data.core.Meta
import slatekit.data.core.Table
import slatekit.data.sql.*
import slatekit.query.Op

object H2Dialect : Dialect(encodeChar = '`') {
}

/**
 * MySql based dialect
 */
open class H2Provider<TId, T>(val meta: Meta<TId, T>, val mapper: Mapper<TId, T>)
    : Provider<TId, T> where TId: kotlin.Comparable<TId>, T: Any {
    override val dialect: Dialect = H2Dialect

    /**
     * The insert statement can be
     */
    override val insert = Insert<TId, T>(H2Dialect, meta, mapper)
    override val update = Update<TId, T>(H2Dialect, meta, mapper)
    override fun select(table: Table): slatekit.query.Select = Builders.Select(dialect, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
    override fun delete(table: Table): slatekit.query.Delete = Builders.Delete(dialect, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
    override fun patch (table: Table): slatekit.query.Update = Builders.Patch(dialect, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
}

fun String.ifNotExists(table:String):String {
    return this.replace("`$table`", "IF NOT EXISTS `$table`")
}
