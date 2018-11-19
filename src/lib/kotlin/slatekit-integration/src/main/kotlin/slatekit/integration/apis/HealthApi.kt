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

package slatekit.integration.apis

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.security.*
import slatekit.common.DateTime
import slatekit.common.Group
import slatekit.common.Health
import slatekit.common.Request
import slatekit.common.info.*
import slatekit.integration.common.AppEntContext

@Api(area = "app", name = "health", desc = "api to get health check information",
        auth = AuthModes.apiKey, roles = "admin", verb = Verbs.auto, protocol = Protocols.all)
class HealthApi(val context: AppEntContext, private val health: Health)  {


    @ApiAction(desc = "simple heartbeat with version info and timestamp")
    fun beat(): Group<Pair<String, String>> = health.heartbeat()


    @ApiAction(desc = "gets detailed info about the application")
    fun info(): List<Group<Pair<String,String>>> = health.info()


    @ApiAction(desc = "get metrics/info about health of this app and relevant dependencies")
    fun check(): Group<Pair<String,String>> = health.check()
}
