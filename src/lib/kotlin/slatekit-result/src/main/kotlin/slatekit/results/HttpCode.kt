package slatekit.results

/**
 * Provides an interface to mark a class / interface as convertible to an Http Status code
 */
interface HttpCode {
    fun toHttpCode(): Int
}
