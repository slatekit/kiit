package slatekit.entities.core

import slatekit.common.encrypt.Encryptor
import slatekit.common.naming.Namer
import slatekit.meta.models.Model
import slatekit.query.Query
import kotlin.reflect.KClass

/**
* @param entityType   : data type of the entity/model
* @param entityIdType : data type of the primary key/identity field
* @param tableName    : name of the table ( defaults to entity name )
* @param encodedChar  : character to encode special names
* @param model        : schema of the entity
* @param namer        : component to handle naming conventions
* @param queryBuilder : creates the query builder specific to a db
*/
data class EntityInfo(
        val entityIdType: KClass<*>,
        val entityType: KClass<*>,
        val tableName:String,
        val encodedChar: Char = '`',
        val model: Model? = null,
        val encryptor: Encryptor? = null,
        val namer: Namer? = null,
        val queryBuilder:(() -> Query)? = null,
        val utcTime:Boolean = false
) {

    fun name():String = ""


    companion object {

        fun memory(idType:KClass<*>, entityType:KClass<*>):EntityInfo {
            val tableName = entityType.simpleName!!.toLowerCase()
            val info = EntityInfo( idType, entityType, tableName )
            return info
        }
    }
}