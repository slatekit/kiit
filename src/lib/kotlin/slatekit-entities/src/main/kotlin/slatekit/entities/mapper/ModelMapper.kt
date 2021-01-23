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

/**
 * Maps an entity to sql and from sql records.
 *
 * @param model
 */
open class ModelMapper<TId, T>(val model: Model, val encoder: ModelEncoder<TId, T>, val decoder: ModelDecoder<TId, T>)
    : Mapper<TId, T> where TId : kotlin.Comparable<TId>, T:Any  {


    override fun encode(model: T, action: DataAction, enc: Encryptor?): Values {
        return encoder.encode(model, action, enc)
    }

    override fun decode(record: Record, enc: Encryptor?): T? {
        return decoder.decode(record, enc)
    }

}
