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
import slatekit.common.Request
import slatekit.common.Serial
import slatekit.common.info.*
import slatekit.integration.common.AppEntContext


@Api(area = "sys", name = "app", desc = "api info about the application and host", roles = "admin", auth = "key-roles", verb = "post", protocol = "*")
class AppApi(val context: AppEntContext)  {

    @ApiAction(desc = "get info about the application", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun about(): About {
        return context.app.about
    }


    @ApiAction(desc = "get info about the application", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun cmd(cmd: Request): About {
        println(cmd.fullName)
        return context.app.about
    }


    @ApiAction(desc = "gets info about the language", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun lang(): Lang {
        return context.app.lang
    }


    @ApiAction(desc = "gets info about the host", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun host(): Host {
        return context.app.host
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


    //@ApiAction(desc = "gets all info", roles = "@parent", verb = "@parent", protocol = "@parent")
    //fun all(): String {
    //    return Serial().serialize(context.app)
    //}
}
