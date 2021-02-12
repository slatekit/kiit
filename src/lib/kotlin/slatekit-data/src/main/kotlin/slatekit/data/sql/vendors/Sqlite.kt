package slatekit.data.sql.vendors

import slatekit.data.Mapper
import slatekit.data.core.Meta
import slatekit.data.core.Table
import slatekit.data.sql.*

object SqliteDialect : Dialect(encodeChar = '"') {
}

/**
 * Sqlite based dialect, this has limited data types
 * see: https://www.sqlite.org/datatype3.html
 * 1. null
 * 2. integer
 * 3. real
 * 4. text
 * 5. blob
 */
open class SqliteProvider<TId, T>(val meta: Meta<TId, T>, val mapper: Mapper<TId, T>)
    : Provider<TId, T> where TId: kotlin.Comparable<TId>, T: Any {
    override val dialect: Dialect = SqliteDialect

    /**
     * The insert statement can be
     */
    override val insert = Insert<TId, T>(SqliteDialect, meta, mapper)
    override val update = Update<TId, T>(SqliteDialect, meta, mapper)
    override fun select(table: Table): slatekit.query.Select = Builders.Select(dialect, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
    override fun delete(table: Table): slatekit.query.Delete = Builders.Delete(dialect, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
    override fun patch (table: Table): slatekit.query.Update = Builders.Patch(dialect, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
}
