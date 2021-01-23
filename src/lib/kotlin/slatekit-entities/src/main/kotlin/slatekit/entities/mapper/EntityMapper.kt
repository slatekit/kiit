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

import slatekit.common.Record
import slatekit.common.crypto.Encryptor
import slatekit.common.data.DataAction
import slatekit.meta.models.Model
import slatekit.common.data.Mapper
import slatekit.common.data.Values
import slatekit.data.core.Meta
import slatekit.data.encoders.Encoders
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
}
