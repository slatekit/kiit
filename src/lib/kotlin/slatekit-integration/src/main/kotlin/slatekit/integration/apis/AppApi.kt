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
import slatekit.apis.middleware.Tracked
import slatekit.common.Random
import slatekit.common.Request
import slatekit.common.Tracker
import slatekit.common.info.*
import slatekit.integration.common.AppEntContext


@Api(area = "app", name = "info", desc = "api info about the application and host", roles = "admin", auth = "key-roles", verb = "post", protocol = "*")
class AppApi(val context: AppEntContext) : Tracked {

    override val tracker = Tracker<Request, Request, Any, Exception>(Random.guid(), "api-tracker", context.logs.getLogger("api"))


    @ApiAction(desc = "get info about the application", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun stats(): List<String> {
        return tracker.diagnostics().map( { it.first + ":" + it.second } )
    }


    @ApiAction(desc = "get info about the application", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun about(): About {
        return context.app.about
    }


    @ApiAction(desc = "gets info about this build", roles = "*", verb = "get", protocol = "@parent")
    fun build(): Build {
        return context.app.build
    }


    @ApiAction(desc = "get info about the application", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun cmd(cmd: Request): About {
        return context.app.about
    }


    @ApiAction(desc = "gets info about the language", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun lang(): Lang {
        return context.app.lang
    }


    @ApiAction(desc = "gets info about the folders", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun dirs(): Folders {
        return context.dirs ?: Folders.Folders.none
    }


    @ApiAction(desc = "gets info about the start up time", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun start(): StartInfo {
        return context.app.start
    }


    @ApiAction(desc = "gets info about the status", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun status(): Status {
        return context.app.status
    }
}
