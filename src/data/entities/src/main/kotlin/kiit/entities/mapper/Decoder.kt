package kiit.entities.mapper

import kiit.common.values.Record
import kiit.common.crypto.Encryptor

interface Decoder<TId, T> where TId : kotlin.Comparable<TId>, T : Any {
    fun decode(record: Record, enc: Encryptor?): T?
}

