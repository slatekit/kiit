package slatekit.data.core

interface Id<TId, T> where TId : Comparable<TId>, T: Any {

    /**
     * Name of the field representing the primary key
     */
    fun id(): String

    /**
     * Determines if the model is persisted
     */
    fun isPersisted(model:T):Boolean

    /**
     * Determines the identity of the model
     */
    fun identity(model:T):TId

    /**
     * Converts the generated id ( from a database ) to the proper type of TId
     */
    fun convertToId(id: String): TId
}


/**
 * Default support for a model based on a long id
 */
class LongId<T>(val op:(T) -> Long) : Id<Long, T> where T: Any {

    /**
     * Name of the field representing the primary key
     */
    override fun id(): String { return "id" }

    /**
     * Determines if the model is persisted
     */
    override fun isPersisted(model:T):Boolean = op(model) > 0L

    /**
     * Determines the identity of the model
     */
    override fun identity(model:T):Long = op(model)

    /**
     * Converts the generated id ( from a database ) to the proper type of TId
     */
    override fun convertToId(id: String): Long = id.toLong()
}
