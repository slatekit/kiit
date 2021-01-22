package slatekit.data.features

import slatekit.data.core.Meta

interface Inspectable<TId, T> where TId : Comparable<TId>, T:Any {
    val meta: Meta<TId, T>
}
