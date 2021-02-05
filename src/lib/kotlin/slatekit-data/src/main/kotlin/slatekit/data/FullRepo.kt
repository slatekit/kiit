package slatekit.data

import slatekit.data.features.*
import slatekit.data.features.Scalarable

/**
 * SQL based Repository ( representing a database table )
 */
interface FullRepo<TId, T>:
    CrudRepo<TId, T>,
    Countable<TId, T>,
    Deletable<TId, T>,
    Findable<TId, T>,
    Orderable<TId, T>,
    Patchable<TId, T>,
    Scalarable<TId, T>
    where TId : Comparable<TId>, T: Any {
}
