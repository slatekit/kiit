package slatekit.data.core

/**
 * Used to operate on the Id ( primary key ) of a model ( class )
 * This allows us to not put any constraints on the model T.
 */
interface Id<TId, T> where TId : Comparable<TId>, T: Any {

    /**
     * Name of the field representing the primary key
     */
    fun name(): String

    /**
     * Determines if the model is persisted
     */
    fun isPersisted(model:T):Boolean

    /**
     * Determines if the model is persisted
     */
    fun isPersisted(id:TId):Boolean

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
 * Long based id support for a model
 */
class LongId<T>(val idName:String = "id", val op:(T) -> Long) : Id<Long, T> where T: Any {

    /**
     * Name of the field representing the primary key
     */
    override fun name(): String { return idName }

    /**
     * Determines if the model is persisted
     */
    override fun isPersisted(model:T):Boolean = op(model) > 0L

    /**
     * Determines if the model is persisted
     */
    override fun isPersisted(id:Long):Boolean = id > 0L

    /**
     * Determines the identity of the model
     */
    override fun identity(model:T):Long = op(model)

    /**
     * Converts the generated id ( from a database ) to the proper type of TId
     */
    override fun convertToId(id: String): Long = id.toLong()
}
