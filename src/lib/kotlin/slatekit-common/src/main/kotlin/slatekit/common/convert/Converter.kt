package slatekit.common.convert


/**
 * Generic converter from an input source type S (Source) to an output target type T (Target)
 * NOTES: Tthis interface
 * @param S: Source e.g. Data model such as User
 * @param T: Target e.g. JSON object
 */
interface Converter<S,T> {

    /**
     * The full type associated with T.
     */
    val cls:Class<*>

    /**
     * Converts from an input source type to an target output type
     */
    fun convert(input:S?):T?

    /**
     * Restores from a target output type to the source type
     */
    fun restore(output:T?):S?
}
