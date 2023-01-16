/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */


package kiit.entities.mapper

import kiit.common.values.Record
import kiit.common.crypto.Encryptor
import kiit.common.data.DataAction
import kiit.common.data.DataType
import kiit.meta.models.Model
import kiit.data.Mapper
import kiit.common.data.Values
import kiit.data.core.Meta
import kiit.data.encoders.Encoders
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
                                val settings: EntitySettings,
                                val encoders: Encoders<TId, T> = Encoders(settings.utcTime),
                                val encoder: Encoder<TId, T> = EntityEncoder(model, meta, settings, null, encoders),
                                val decoder: Decoder<TId, T> = EntityDecoder(model, meta, idClass, enClass, settings, null))
    : Mapper<TId, T> where TId : kotlin.Comparable<TId>, T:Any  {

    /**
     * Gets the table column name mapped to the field name
     */
    override fun column(field:String): String = model.lookup[field]?.storedName ?: field

    /**
     * Gets the data type of the field
     */
    override fun datatype(field:String): DataType {
        return when(val mappedField = model.lookup[field]){
            null -> throw Exception("Field not mapped! model=${model.name}, field=$field")
            else -> mappedField.dataTpe
        }
    }

    /**
     * Gets the encode values for the model to be used for building a sql statement
     */
    override fun encode(model: T, action: DataAction, enc: Encryptor?): Values {
        return encoder.encode(model, action, enc)
    }

    /**
     * Decodes the record into the model
     */
    override fun decode(record: Record, enc: Encryptor?): T? {
        return decoder.decode(record, enc)
    }
}
