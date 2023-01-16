package kiit.entities.mapper

import kiit.common.crypto.Encryptor
import kiit.common.data.DataAction
import kiit.common.data.Values


interface Encoder<TId, T> where TId : kotlin.Comparable<TId>, T : Any {
    fun encode(item: T, action: DataAction, enc: Encryptor?): Values
}



