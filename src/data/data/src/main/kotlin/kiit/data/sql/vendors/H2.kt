package kiit.data.sql.vendors

import kiit.data.Mapper
import kiit.data.core.Meta
import kiit.data.core.Table
import kiit.data.sql.*
import kiit.query.Op

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
    override fun select(table: Table): kiit.query.Select = Builders.Select(dialect, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
    override fun delete(table: Table): kiit.query.Delete = Builders.Delete(dialect, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
    override fun patch (table: Table): kiit.query.Update = Builders.Patch(dialect, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
}

fun String.ifNotExists(table:String):String {
    return this.replace("`$table`", "IF NOT EXISTS `$table`")
}
