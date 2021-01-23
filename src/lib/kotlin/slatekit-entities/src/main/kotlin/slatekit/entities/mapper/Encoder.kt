package slatekit.entities.mapper

import slatekit.common.crypto.Encryptor
import slatekit.common.data.DataAction
import slatekit.common.data.Values


interface Encoder<TId, T> where TId : kotlin.Comparable<TId>, T : Any {
    fun encode(item: T, action: DataAction, enc: Encryptor?): Values
}



