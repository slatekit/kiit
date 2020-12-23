package slatekit.common.convert

/**
 * Interface for transforming a source type S to a converted target type T or encoded type E.
 * This allows for both conversion from
 * 1. a data model to say JSON object      ( via converter interface )
 * 2. a data model to say JSON string/text ( via encoder interface   )
 *
 * @param S: Source type   e.g. User
 * @param T: Target type   e.g. JSON
 * @param E: Encode type   e.g. String
 *
 * @sample
 * val transfomer = UserTransformer
 */
interface Transformer<S,T,E> : Converter<S, T>, Encoder<S, E> {
}