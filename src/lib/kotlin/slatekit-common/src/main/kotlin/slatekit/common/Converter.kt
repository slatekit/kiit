package slatekit.common


/**
 * Generic converter from an input source type S (Source) to an output target type T (Target)
 */
interface Converter<S,T> {

    /**
     * The full type name associated with T.
     */
    val name:String

    /**
     * Converts from an input source type to an target output type
     */
    fun convert(input:S?):T?

    /**
     * Restores from a target output type to the source type
     */
    fun restore(output:T?):S?
}