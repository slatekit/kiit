package kiit.data.sql.vendors

import kiit.common.data.DataType
import kiit.common.data.DataTypeMap
import kiit.data.Mapper
import kiit.data.core.Meta
import kiit.data.core.Table
import kiit.data.sql.*
import kiit.data.syntax.DbTypes
import kiit.query.Op

object H2Dialect : Dialect(types = H2Types, encodeChar = '`') {
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
    override fun select(table: Table): kiit.query.Select = Builders.Select(dialect, meta.table.schema, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
    override fun delete(table: Table): kiit.query.Delete = Builders.Delete(dialect, meta.table.schema, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
    override fun patch (table: Table): kiit.query.Update = Builders.Patch(dialect, meta.table.schema, meta.table.name, { name -> mapper.datatype(name)}, { name -> mapper.column(name) })
}

fun String.ifNotExists(table:String):String {
    return this.replace("`$table`", "IF NOT EXISTS `$table`")
}

object H2Types : DbTypes() {

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

