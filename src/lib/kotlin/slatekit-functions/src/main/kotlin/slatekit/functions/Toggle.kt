package slatekit.functions



/**
 * Allows enabling/disabling of a feature/middleware.
 * This is designed for use at runtime
 */
interface Toggle {

    fun toggle(on:Boolean)

    fun enable()  = toggle(true)

    fun disable() = toggle(false)
}