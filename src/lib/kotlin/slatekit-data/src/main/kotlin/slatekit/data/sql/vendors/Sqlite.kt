package slatekit.data.sql.vendors

import slatekit.common.data.DataType
import slatekit.data.Mapper
import slatekit.data.core.Meta
import slatekit.data.core.Table
import slatekit.data.encoders.*
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
 *
 * ENUM
 * enum           -> int
 *
 * Dates
 * local date     -> int
 * local time     -> int
 * local datetime -> long epoch millis
 * zoned datetime -> long epoch millis
 *
 * UUIDS
 * uuid           -> string
 * upid           -> string
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


/**
 * Stores all the encoders for all supported data types
 * This converts the following:
 *
 * short          -> int
 * long           -> real
 * float          -> real
 * local date     -> int
 * local time     -> int
 * local datetime -> long epoch millis
 * zoned datetime -> long epoch millis
 */
open class SqliteEncoders<TId, T>(utc:Boolean) : Encoders<TId, T>(utc) where TId: kotlin.Comparable<TId>, T:Any {
    override val bools              = BoolEncoder(DataType.DTInt)
    override val shorts             = ShortEncoder(DataType.DTInt)
    override val ints               = IntEncoder(DataType.DTLong)
    override val localDates         = LocalDateEncoder(DataType.DTInt)
    override val localTimes         = LocalTimeEncoder(DataType.DTInt)
    override val localDateTimes     = LocalDateTimeEncoder(DataType.DTLong)
    override val zonedDateTimes     = ZonedDateTimeEncoder(DataType.DTLong, utc)
    override val dateTimes          = DateTimeEncoder(DataType.DTLong, utc)
    override val instants           = InstantEncoder(DataType.DTLong)
}



fun String.sqliteIfNotExists(table:String):String {
    return this.replace("\"$table\"", "IF NOT EXISTS \"$table\"")

}
