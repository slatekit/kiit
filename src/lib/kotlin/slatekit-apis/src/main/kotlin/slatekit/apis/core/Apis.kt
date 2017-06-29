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

package slatekit.apis.core

import slatekit.apis.ApiBase
import slatekit.common.ListMap


/**
 * Container for all the registered apis that can be called dynamically.
 * This contains a lookup of api names to the actual apis.
 * e.g.
 * {
 *   "invites" => InvitesApi()
 *   "devices" => DevicesApi()
 * }
 */
class Apis(items: List<Pair<String, ApiBase>> = listOf()) : ListMap<String, ApiBase>(items)