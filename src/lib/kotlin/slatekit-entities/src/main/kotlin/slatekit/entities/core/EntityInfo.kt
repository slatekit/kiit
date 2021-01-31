package slatekit.entities.core

import kotlin.reflect.KClass
import slatekit.common.crypto.Encryptor
import slatekit.common.naming.Namer
import slatekit.data.core.Meta
import slatekit.meta.models.Model
import slatekit.query.Query

/**
* @param modelType : data type of the entity/model
* @param idType : data type of the primary key/identity field
* @param tableName : name of the table ( defaults to entity name )
* @param encodedChar : character to encode special names
* @param model : schema of the entity
* @param namer : component to handle naming conventions
* @param queryBuilder : creates the query builder specific to a db
*/
data class EntityInfo(
    val idType: KClass<*>,
    val modelType: KClass<*>,
    val meta: Meta<*, *>,
    val model: Model? = null,
    val encryptor: Encryptor? = null,
    val namer: Namer? = null,
    val queryBuilder: (() -> Query)? = null,
    val utcTime: Boolean = false
) {

    fun name(): String = ""
}
