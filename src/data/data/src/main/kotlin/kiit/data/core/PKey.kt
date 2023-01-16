package kiit.data.core

import kiit.common.data.DataType

/**
 * Primary key info
 * @param name: Name of the primary key e.g. "id"
 * @param type: Type of the primary key e..g "long"
 * NOTE: These are the defaults for Slate Kit, but can be customized here
 */
data class PKey(val name:String = "id", val type: DataType = DataType.DTLong)
