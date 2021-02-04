package slatekit.data.sql.vendors

import slatekit.common.data.Mapper
import slatekit.data.core.Meta
import slatekit.data.core.Table
import slatekit.data.sql.*


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
    override fun select(table: Table): slatekit.query.Select = Builders.Select(meta.table.name, null, null)
    override fun patch (table: Table): slatekit.query.Update = Builders.Patch(meta.table.name, null, null)
    override fun delete(table: Table): slatekit.query.Delete = Builders.Delete(meta.table.name, null, null)


    companion object {
        val MySqlDialect = Dialect(encodeChar = '`')
    }
}
