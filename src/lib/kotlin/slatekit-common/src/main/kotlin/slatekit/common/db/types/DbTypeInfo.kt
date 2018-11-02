package slatekit.common.db.types

import slatekit.common.db.DbFieldType
import kotlin.reflect.KClass

data class DbTypeInfo(val metaType: DbFieldType,
           val dbType: String,
           val langType:Class<*>)