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

package slatekit.common.app

import slatekit.common.Result
import slatekit.common.results.ResultFuncs.success
import slatekit.common.results.ResultFuncs.yes

interface AppLifeCycle {


    /**
     * initializes the application
     *
     */
    fun init(): Result<Boolean> = yes()


    /**
     * executes the application
     *
     * @return
     */
    fun exec(): Result<Any> = success("")


    /**
     * shutdown hook to stop any services
     */
    fun end(): Unit {}
}
