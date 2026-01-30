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
import kiit.data.syntax.DbTypes
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
object PostgresDialect : Dialect(types = PostgresTypes, encodeChar = '"', useSchema = true) {

    override fun op(op: Op): String {
        return when (op) {
            Op.Is -> "is"
            Op.IsNot -> "is not"
            else -> op.text
        }
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
    override fun select(table: Table): kiit.query.Select = Builders.Select(dialect, meta.table.schema, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
    override fun delete(table: Table): kiit.query.Delete = Builders.Delete(dialect, meta.table.schema, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
    override fun patch (table: Table): kiit.query.Update = Builders.Patch(dialect, meta.table.schema, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
}


object PostgresTypes : DbTypes() {
    /**
     * BOOL
     */
    override val boolType   = DataTypeMap(DataType.DTBool  , "BOOLEAN", Types.JBoolClass)
    override val stringType = DataTypeMap(DataType.DTString, "VARCHAR", Types.JStringClass)
    override val uuidType   = DataTypeMap(DataType.DTUUID  , "VARCHAR", Types.JStringClass)
    override val ulidType   = DataTypeMap(DataType.DTULID  , "VARCHAR", Types.JStringClass)
    override val upidType   = DataTypeMap(DataType.DTUPID  , "VARCHAR", Types.JStringClass)
    override val localDateTimeType = DataTypeMap(DataType.DTLocalDateTime, "TIMESTAMP", Types.JLocalDateTimeClass)
    override val zonedDateTimeType = DataTypeMap(DataType.DTZonedDateTime, "TIMESTAMPTZ", Types.JZonedDateTimeClass)
    override val dateTimeType = DataTypeMap(DataType.DTDateTime, "TIMESTAMPTZ", Types.JDateTimeClass)
    override val doubleType = DataTypeMap(DataType.DTDouble, "FLOAT8", Types.JDoubleClass)



    override val lookup:Map<DataType, DataTypeMap> = mapOf(
        boolType.metaType to boolType,
        charType.metaType to charType,
        stringType.metaType to stringType,
        textType.metaType to textType,
        uuidType.metaType to uuidType,
        shortType.metaType to shortType,
        intType.metaType to intType,
        longType.metaType to longType,
        floatType.metaType to floatType,
        doubleType.metaType to doubleType,
        //decimalType.metaType to decimalType,
        localdateType.metaType to localdateType,
        localtimeType.metaType to localtimeType,
        localDateTimeType.metaType to localDateTimeType,
        zonedDateTimeType.metaType to zonedDateTimeType,
        dateTimeType.metaType to dateTimeType,
        instantType.metaType to instantType,
        uuidType.metaType to uuidType,
        ulidType.metaType to ulidType,
        upidType.metaType to upidType
    )
}
