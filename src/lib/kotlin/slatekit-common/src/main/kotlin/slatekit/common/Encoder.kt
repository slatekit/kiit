
package slatekit.common

/**
 * Interface for encoding/decoding from source type S to output target type String
 * @param S: Source e.g. Data model such as User
 * @param T: Target e.g. String
 */
interface Encoder<S,T> {

    /**
     * The full type associated with T.
     */
    val cls:Class<*>

    /**
     * Decodes
     */
    fun encode(data:S?):T?


    /**
     * Decodes
     */
    fun decode(item:T?):S?
}