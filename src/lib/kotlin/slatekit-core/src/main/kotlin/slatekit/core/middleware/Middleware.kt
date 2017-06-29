/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.core.middleware


import slatekit.common.info.About
import slatekit.common.results.ResultFuncs.ok
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Base trait for the 3 different types of middle ware ( hooks, filters, controls )
 * 1. hooks   : for pre/post execution of api actions         ( can not modify the execution    )
 * 2. filters : for allowing/denying execution of api action  ( can only allow/deny execution   )
 * 3. controls: for controlling the execution of an api action( can handle the execution itself )
 */
abstract class Middleware {
    /**
     * Info about the middleware including:
     * id, name, desc, company, version, url, etc.
     */
    abstract val about: About


    /**
     * common/basic return values for the filter.
     * Used as values here to avoid excessive object creation
     */
    val success = ok()

    /**
     * internal flag to enable/disable this middleware
     */
    private val flag = AtomicBoolean(true)


    /**
     * Enables this middleware
     * @return
     */
    fun enable(): Boolean = toggle(true)


    /**
     * Disables this middleware
     * @return
     */
    fun disable(): Boolean = toggle(false)


    /**
     * toggle this middleware
     * @param newValue
     * @return
     */
    fun toggle(newValue: Boolean): Boolean {
        flag.set(newValue)
        return flag.get()
    }
}
