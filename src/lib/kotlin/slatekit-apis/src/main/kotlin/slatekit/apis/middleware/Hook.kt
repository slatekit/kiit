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

package slatekit.apis.middleware

import slatekit.apis.ApiRequest
import slatekit.functions.middleware.After
import slatekit.functions.middleware.Before
import slatekit.functions.middleware.Middleware

/**
 * A "Hooks" based middle-ware that allows only handling before/after events
 * of an API call, without any modification to the life-cycle/flow.
 *
 * NOTE: The hooks are applied right before and after the call to the action
 *
 */
interface Hook : Middleware, Before<ApiRequest>, After<ApiRequest, Any?>
