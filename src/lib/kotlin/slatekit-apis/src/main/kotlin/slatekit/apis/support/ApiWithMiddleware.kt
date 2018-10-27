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

import slatekit.apis.middleware.Filter
import slatekit.apis.middleware.Hook

typealias Error = slatekit.apis.middleware.Error

/**
 * Base class for an Api with all the middleware ( hooks, filters, errors )
 */
interface ApiWithMiddleware : Api, Hook, Filter, Error