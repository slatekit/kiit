package slatekit.entities.databases.mysql

import slatekit.common.Types
import slatekit.common.db.DbFieldType
import slatekit.common.db.types.DbTypeInfo

object MySqlTypes {

    val stringType = DbTypeInfo(DbFieldType.DbString, "NVARCHAR", Types.JStringClass)
    val boolType = DbTypeInfo(DbFieldType.DbBool, "BIT" , Types.JBoolClass)
    val shortType = DbTypeInfo(DbFieldType.DbShort, "TINYINT", Types.JStringClass)
    val intType = DbTypeInfo(DbFieldType.DbNumber, "INTEGER", Types.JStringClass)
    val longType = DbTypeInfo(DbFieldType.DbLong, "BIGINT", Types.JStringClass)
    val floatType = DbTypeInfo(DbFieldType.DbFloat, "FLOAT", Types.JStringClass)
    val doubleType = DbTypeInfo(DbFieldType.DbDouble, "DOUBLE", Types.JStringClass)
    val decimalType = DbTypeInfo(DbFieldType.DbReal, "DECIMAL", Types.JStringClass)
    val localdateType = DbTypeInfo(DbFieldType.DbLocalDate, "DATE", Types.JStringClass)
    val localtimeType = DbTypeInfo(DbFieldType.DbLocalTime, "TIME", Types.JStringClass)
    val localDateTimeType = DbTypeInfo(DbFieldType.DbLocalDateTime, "DATETIME", Types.JStringClass)
    val zonedDateTimeType = DbTypeInfo(DbFieldType.DbZonedDateTime, "DATETIME", Types.JStringClass)
    val instantType = DbTypeInfo(DbFieldType.DbInstant, "INSTANT", Types.JStringClass)
    val dateTimeType = DbTypeInfo(DbFieldType.DbDateTime, "DATETIME", Types.JStringClass)
}