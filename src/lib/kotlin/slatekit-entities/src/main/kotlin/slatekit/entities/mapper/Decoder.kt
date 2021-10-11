package slatekit.entities.mapper

import slatekit.common.values.Record
import slatekit.common.crypto.Encryptor

interface Decoder<TId, T> where TId : kotlin.Comparable<TId>, T : Any {
    fun decode(record: Record, enc: Encryptor?): T?
}

