package kiit.data.kiit.data.sql.vendors

import kiit.data.Mapper
import kiit.data.core.Meta
import kiit.data.core.Table
import kiit.data.sql.*
import kiit.query.Op

/**
 * NOTES
 * 1. https://www.postgresql.org/docs/current/sql-syntax-lexical.html#SQL-SYNTAX-IDENTIFIERS
 * 2. https://www.postgresql.org/docs/current/datatype.html
 * 3. https://www.baeldung.com/java-postgresql-store-date-time
 *
 * CONVERSIONS:
 * bigint          = bigserial ( primary key only )
 * nvarchar        = varchar
 * double          = float8
 * local datetime  = timestamp
 * zoned datetime  = timestamptz
 */
object PostgresDialect : Dialect(encodeChar = '"', useSchema = true) {

    override fun op(op: Op): String =
        when (op) {
            Op.Is -> "is"
            Op.IsNot -> "is not"
            else -> op.text
        }
}

/**
 * Postgres based dialect
 */
open class PostgresProvider<TId, T>(val meta: Meta<TId, T>, val mapper: Mapper<TId, T>)
    : Provider<TId, T> where TId: kotlin.Comparable<TId>, T: Any {
    override val dialect: Dialect = PostgresDialect

    /**
     * The insert statement can be
     */
    override val insert = Insert<TId, T>(PostgresDialect, meta, mapper)
    override val update = Update<TId, T>(PostgresDialect, meta, mapper)
    override fun select(table: Table): kiit.query.Select = Builders.Select(dialect, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
    override fun delete(table: Table): kiit.query.Delete = Builders.Delete(dialect, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
    override fun patch (table: Table): kiit.query.Update = Builders.Patch(dialect, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
}
