/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A Kotlin Tool-Kit for Server + Android
 *  </kiit_header>
 */
package kiit.results

/**
 * Alias for Result<T,E> to avoid collision with the Kotlin Result type
 */

typealias Expect<T, E> = kiit.results.Result<T, E>

/**
 * Alias for Result<T,E> defaulting the E error type ( [Failure] branch ) to [Unit]
 *
 * This allows for :
 * 1. Representing null using Option
 * 2. Allows for mapping Option<T> to Outcome<T>
 * 3. Can provide additional info on why something is null via status
 */
typealias Option<T> = Result<T, Unit>

/**
 * Alias for Result<T,E> defaulting the E error type ( [Failure] branch ) to [Exception]
 *
 * This allows for :
 * 1. slightly easier usage by only requiring 1 type parameter
 * 2. avoid collision with the Kotlin Result type
 * 3. similarity to Try type available in other languages like Scala
 */
typealias Try<T> = Result<T, Throwable>

/**
 * Alias for Result<T,E> defaulting the E error type ( [Failure] branch ) to [String]
 *
 * This allows for :
 * 1. slightly easier usage by only requiring 1 type parameter
 * 2. avoid collision with the Kotlin Result type
 * 3. use a simple [String] for failures without having to build [Err] or [Exception]
 */
typealias Notice<T> = Result<T, String>

/**
 * Alias for Result<T,E> defaulting the E error type ( [Failure] branch ) to [Err] interface
 *
 * This allows for :
 * 1. slightly easier usage by only requiring 1 type parameter
 * 2. avoid collision with the Kotlin Result type
 * 3. allows for using the sensible default implementations for [Err]
 */
typealias Outcome<T> = Result<T, Err>

/**
 * Alias for Result<T,E> defaulting the E error type ( [Failure] branch ) to [Err.ErrorList]
 *
 * This allows for :
 * 1. Is to be used for validation purposes
 * 2. Collecting multiple errors
 */
typealias Validated<T> = Result<T, Err.ErrorList>
