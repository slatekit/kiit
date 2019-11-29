package slatekit.common.db


data class DbTypeInfo(
    val metaType: DbFieldType,
    val dbType: String,
    val langType: Class<*>
)
