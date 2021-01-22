package slatekit.data.core

interface Table<TId> where TId : Comparable<TId> {
    /**
     * Name of id / primary key of table
     */
    fun id(): String


    /**
     * Name of the table
     */
    fun name(): String
}


