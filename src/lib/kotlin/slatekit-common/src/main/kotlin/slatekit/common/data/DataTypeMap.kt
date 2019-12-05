package slatekit.common.data


data class DataTypeMap(
        val metaType: DataType,
        val dbType: String,
        val langType: Class<*>
)
