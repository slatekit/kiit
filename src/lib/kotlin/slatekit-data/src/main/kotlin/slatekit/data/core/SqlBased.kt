package slatekit.data.core

import slatekit.common.data.IDb
import slatekit.common.data.Mapper

interface SqlBased<TId, T> where TId : Comparable<TId> {
    val db: IDb
    val mapper: Mapper<TId, T>
}
