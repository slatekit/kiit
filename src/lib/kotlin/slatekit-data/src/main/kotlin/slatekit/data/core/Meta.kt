package slatekit.data.core


/**
 * Holds relevent meta info about table and the id for the model
 * @param: id : Provides operations related to the id the model
 * @param: table: Info about the table
 *
 * NOTES:
 * 1. This does NOT use REFLECTION, you must provide an implementation for Id<TId, T>
 * 2. Slate Kit provides defaults for Id<TId, T> where the id is Long @see[slatekit.data.core.LongId]
 */
data class Meta<TId, T>(val id:Id<TId, T>, val table: Table) where TId : Comparable<TId>, T : Any {
    /**
     * Name of the table for convenience
     */
    val name: String
        get() {
            return table.name
        }


    /**
     * Name of the Id/Primary key for convenience
     */
    val pkey: PKey
        get() {
            return table.pkey
        }
}


