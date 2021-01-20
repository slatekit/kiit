package slatekit.data.core

import slatekit.common.crypto.Encryptor
import slatekit.common.naming.Namer
import slatekit.meta.models.Model
import slatekit.query.Query
import kotlin.reflect.KClass

class EntityInfo(
    val idType: KClass<*>,
    val modelType: KClass<*>,
    val tableName: String,
    val encodedChar: Char = '`',
    val model: Model? = null,
    val encryptor: Encryptor? = null,
    val namer: Namer? = null,
    val queryBuilder: (() -> Query)? = null,
    val utcTime: Boolean = false,
    val idInfo: EntityId = EntityId()
) {

    fun name(): String = ""

    companion object {

        fun memory(idType: KClass<*>, entityType: KClass<*>): EntityInfo {
            val tableName = entityType.simpleName!!.toLowerCase()
            val info = EntityInfo(idType, entityType, tableName)
            return info
        }
    }
}
