/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */


package slatekit.entities.mapper

import slatekit.common.Field
import slatekit.common.Id
import slatekit.common.Record
import slatekit.common.crypto.Encryptor
import slatekit.common.data.DataAction
import slatekit.meta.models.Model
import slatekit.common.data.Mapper
import slatekit.common.data.Values
import slatekit.common.naming.Namer
import slatekit.data.core.Meta
import slatekit.data.encoders.Encoders
import slatekit.meta.Reflector
import slatekit.meta.models.ModelField
import kotlin.reflect.KClass

/**
 * Maps an entity to sql and from sql records.
 *
 * @param model
 */
open class EntityMapper<TId, T>(val model: Model,
                                val meta:Meta<TId, T>,
                                val idClass: KClass<TId>,
                                val enClass: KClass<T>,
                                val settings: EntitySettings = EntitySettings(true),
                                val encoder: Encoder<TId, T> = EntityEncoder(model, meta, settings, null, Encoders()),
                                val decoder: Decoder<TId, T> = EntityDecoder(model, meta, idClass, enClass, settings, null))
    : Mapper<TId, T> where TId : kotlin.Comparable<TId>, T:Any  {


    override fun encode(model: T, action: DataAction, enc: Encryptor?): Values {
        return encoder.encode(model, action, enc)
    }

    override fun decode(record: Record, enc: Encryptor?): T? {
        return decoder.decode(record, enc)
    }


    companion object {
        /**
         * Builds a schema ( Model ) from the Class/Type supplied.
         * NOTE: The mapper then works off the Model class for to/from mapping of data to model.
         * @param dataType
         * @return
         */
        @JvmStatic
        fun loadSchema(dataType: KClass<*>, idFieldName: String? = null, namer: Namer? = null, table: String? = null): Model {
            val modelName = dataType.simpleName ?: ""
            val modelNameFull = dataType.qualifiedName ?: ""

            // Get Id
            val idFields = Reflector.getAnnotatedProps<Id>(dataType, Id::class)
            val idField = idFields.firstOrNull()

            // Now add all the fields.
            val matchedFields = Reflector.getAnnotatedProps<Field>(dataType, Field::class)

            // Loop through each field
            val withAnnos = matchedFields.filter { it.second != null }
            val fields = withAnnos.map { matchedField ->
                val modelField = ModelField.ofData(matchedField.first, matchedField.second!!,
                    namer, idField == null, idFieldName)
                val finalModelField = if (!modelField.isBasicType()) {
                    val model = loadSchema(modelField.dataCls, namer = namer)
                    modelField.copy(model = model)
                } else modelField
                finalModelField
            }
            val allFields = when(idField) {
                null -> fields
                else -> mutableListOf(ModelField.ofId(idField.first, "", namer)).plus(fields)
            }
            return Model(modelName, modelNameFull, dataType, modelFields = allFields, namer = namer, tableName = table ?: modelName)
        }
    }
}
