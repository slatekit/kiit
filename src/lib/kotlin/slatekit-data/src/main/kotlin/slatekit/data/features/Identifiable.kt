package slatekit.data.features

interface Identifiable<TId> where TId : Comparable<TId> {
    /**
     * Name of id / primary key of table
     */
    fun id(): String


    /**
     * Name of the table
     */
    fun name(): String
}


