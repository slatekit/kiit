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
import slatekit.apis.middleware.Tracked
import slatekit.common.Random
import slatekit.common.Request
import slatekit.common.utils.Tracker
import slatekit.common.info.*
import slatekit.integration.common.AppEntContext

@Api(area = "app", name = "info", desc = "api info about the application and host",
        auth = AuthModes.apiKey, roles = "admin", verb = Verbs.auto, protocol = Protocols.all)
class AppApi(val context: AppEntContext) : Tracked {

    override val tracker = Tracker<Request, Request, Any, Exception>(
        Random.guid(),
        "api-tracker"
    )

    @ApiAction(desc = "gets info about this build", roles = "*", verb = "get", protocol = "@parent")
    fun build(): Build {
        return context.app.build
    }

    @ApiAction(desc = "get info about the application")
    fun stats(): List<String> {
        return listOf( tracker.lastRequest.get(),
                tracker.lastSuccess.get(),
                tracker.lastFailure.get(),
                tracker.lastFiltered.get()
        ).map { it.toString() }
    }

    @ApiAction(desc = "get info about the application")
    fun about(): About {
        return context.app.about
    }

    @ApiAction(desc = "get info about the application")
    fun cmd(cmd: Request): About {
        return context.app.about
    }

    @ApiAction(desc = "gets info about the language")
    fun lang(): Lang {
        return context.app.lang
    }

    @ApiAction(desc = "gets info about the folders")
    fun dirs(): Folders {
        return context.dirs ?: Folders.Folders.none
    }

    @ApiAction(desc = "gets info about the start up time")
    fun start(): StartInfo {
        return context.app.start
    }

    @ApiAction(desc = "gets info about the status")
    fun status(): Status {
        return context.app.status
    }
}
