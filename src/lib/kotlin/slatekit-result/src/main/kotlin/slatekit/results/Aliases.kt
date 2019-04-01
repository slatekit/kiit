package slatekit.results


/**
 * Alias for Result<T,E> defaulting the E error type ( [Failure] branch ) to [Exception]
 *
 * This allows for :
 * 1. slightly easier usage by only requiring 1 type parameter
 * 2. avoid collision with the Kotlin Result type
 * 3. similarity to Try type available in other languages like Scala
 */
typealias Try<T> = Result<T, Exception>


/**
 * Alias for Result<T,E> defaulting the E error type ( [Failure] branch ) to [Err] interface
 *
 * This allows for :
 * 1. slightly easier usage by only requiring 1 type parameter
 * 2. avoid collision with the Kotlin Result type
 * 3. uses an empty marker interface [Err] with default implementations available
 */
typealias Outcome<T> = Result<T, Err>


/**
 * Alias for Result<T,E> defaulting the E error type ( [Failure] branch ) to [String]
 *
 * This allows for :
 * 1. slightly easier usage by only requiring 1 type parameter
 * 2. avoid collision with the Kotlin Result type
 * 3. use a simple [String] for failures without having to build [Err] or [Exception]
 */
typealias Notice<T> = Result<T, String>


