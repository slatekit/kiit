package slatekit.db.types

import slatekit.db.DbFieldType

data class DbTypeInfo(val metaType: DbFieldType,
                      val dbType: String,
                      val langType:Class<*>)