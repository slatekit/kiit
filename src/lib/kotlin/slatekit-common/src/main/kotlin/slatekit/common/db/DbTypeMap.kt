package slatekit.common.db


data class DbTypeMap(
    val metaType: DbType,
    val dbType: String,
    val langType: Class<*>
)
