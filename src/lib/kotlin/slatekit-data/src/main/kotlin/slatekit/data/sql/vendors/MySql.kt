package slatekit.data.sql.vendors

import slatekit.data.Mapper
import slatekit.data.core.Meta
import slatekit.data.core.Table
import slatekit.data.sql.*
import slatekit.query.Op

object MySqlDialect : Dialect(encodeChar = '`') {

    override fun op(op:Op): String =
        when (op) {
            Op.Eq -> "="
            Op.Neq -> "<>"
            Op.IsEq -> "is"
            Op.IsNeq -> "is not"
            else -> op.text
        }
}

/**
 * MySql based dialect
 */
open class MySqlProvider<TId, T>(val meta: Meta<TId, T>, val mapper: Mapper<TId, T>)
    : Provider<TId, T> where TId: kotlin.Comparable<TId>, T: Any {
    override val dialect: Dialect = MySqlDialect

    /**
     * The insert statement can be
     */
    override val insert = Insert<TId, T>(MySqlDialect, meta, mapper)
    override val update = Update<TId, T>(MySqlDialect, meta, mapper)
    override fun select(table: Table): slatekit.query.Select = Builders.Select(dialect, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
    override fun delete(table: Table): slatekit.query.Delete = Builders.Delete(dialect, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
    override fun patch (table: Table): slatekit.query.Update = Builders.Patch(dialect, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
}
