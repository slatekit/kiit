package slatekit.data

import slatekit.data.features.*

/**
 * SQL based Repository ( representing a database table )
 */
interface SqlRepo<TId, T>:
    CrudRepo<TId, T>,
    Countable<TId, T>,
    Deletable<TId, T>,
    Findable<TId, T>,
    Orderable<TId, T>,
    Patchable<TId, T>
    where TId : Comparable<TId> {
}
