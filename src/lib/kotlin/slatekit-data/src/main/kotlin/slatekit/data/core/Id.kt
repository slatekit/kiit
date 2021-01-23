package slatekit.data.core

/**
 * Used to operate on the Id ( primary key ) of a model ( class )
 * This allow us to not but any constraints on the model T.
 */
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
 * Long based id support for a model based
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
