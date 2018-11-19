package slatekit.common

/**
 * Combines a boolean success, string message, and data item T
 *
 *
 * NOTE: You should use the Kotlin Result<T> or the slatekit Result<T,E>
 *       as an alternative to this class to model successes and failures.
 *       This is here as a light-weight representation of either ones above
 *       especially in cases where you don't need composition and/or if
 *       this is used at the edges ( boundary ) of your application.
 *
 * @param success : Indicates a success/expected result
 * @param data    : Data representing the result
 * @param message : String to indicate reason for success/failure
 * @param code    : Optional integer for explicitly representing errors
 */
data class BoolResult<T>(
    val success:Boolean,
    val item   :T?     ,
    val message:String = "",
    val code   :Int        = 0
)