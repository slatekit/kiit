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

package slatekit.apis.support

import slatekit.common.*
import slatekit.common.results.ResultFuncs.failureWithCode
import slatekit.common.results.ResultFuncs.success
import slatekit.common.results.UNEXPECTED_ERROR
import java.io.File


/**
 * Base class for any Api, provides lookup functionality to check for exposed api actions.
 * @param context   : The context of the application ( logger, config, encryptor, etc )
 */
interface ApiWithMiddleware : Api {

    val isErrorEnabled :Boolean
    val isHookEnabled  :Boolean
    val isFilterEnabled:Boolean


    fun onException(context: Context, request: Request, source:Any, ex: Exception): Result<Any> {
        return failureWithCode(UNEXPECTED_ERROR, msg = "unexpected error in api", err = ex)
    }


    /**
     * Hook for before this api handles any request
     */
    fun onBefore(context:Context, request:Request, source:Any, target:Any): Unit {
    }


    /**
     * Hook for after this api handles any request
     */
    fun onAfter(context:Context, request:Request,  source:Any, target:Any): Unit {
    }


    /**
     * Hook to first filter a request before it is handled by this api.
     */
    fun onFilter(context:Context, request:Request, source:Any, target:Any): Result<Any>  {
        return success(true)
    }
}
