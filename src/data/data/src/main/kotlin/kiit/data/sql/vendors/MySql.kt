package kiit.data.sql.vendors

import kiit.common.Types
import kiit.common.data.DataType
import kiit.common.data.DataTypeMap
import kiit.common.data.DbTypeMap
import kiit.common.data.Vendor
import kiit.data.Mapper
import kiit.data.core.Meta
import kiit.data.core.Table
import kiit.data.sql.*
import kiit.query.Op

object MySqlDialect : Dialect(encodeChar = '`') {

    override fun op(op:Op): String {
        return when (op) {
            Op.Is -> "is"
            Op.IsNot -> "is not"
            else -> op.text
        }
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
    override fun select(table: Table): kiit.query.Select = Builders.Select(dialect, meta.table.schema, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
    override fun delete(table: Table): kiit.query.Delete = Builders.Delete(dialect, meta.table.schema, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
    override fun patch (table: Table): kiit.query.Update = Builders.Patch(dialect, meta.table.schema, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
}
