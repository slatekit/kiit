package slatekit.common


/**
 * Generic converter from an input source type S (Source) to an output target type T (Target)
 */
interface Converter<S,T> {

    /**
     * Converts from an input source type to an target output type
     */
    fun convert(input:S):T

    /**
     * Converts from a nullable input source type to a target output type
     */
    fun convertOrNull(input:S?):T? {
        return input?.let { convert(it) }
    }

    /**
     * Restores from a target output type to the source type
     */
    fun restore(output:T):S

    /**
     * Restores from nullable target output type to the source type
     */
    fun restoreOrNull(output:T?):S? {
        return output?.let { restore(it) }
    }
}