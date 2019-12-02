package slatekit.common.data


data class DbTypeMap(
    val metaType: DbType,
    val dbType: String,
    val langType: Class<*>
)
