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

import slatekit.apis.support.ApiWithMiddleware
import slatekit.apis.support.ApiWithSupport
import slatekit.common.*

/**
 * Base class for any Api, provides lookup functionality to check for exposed api actions.
 * @param context   : The context of the application ( logger, config, encryptor, etc )
 */
open class ApiBase(override val context: Context) : ApiWithSupport, ApiWithMiddleware {

    override val isErrorEnabled = false
    override val isHookEnabled = false
    override val isFilterEnabled = false
}
