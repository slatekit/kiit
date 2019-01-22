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
import slatekit.common.requests.Request
import slatekit.common.info.*
import slatekit.integration.common.AppEntContext

@Api(area = "app", name = "info", desc = "api info about the application and host",
        auth = AuthModes.apiKey, roles = "admin", verb = Verbs.auto, protocol = Protocols.all)
class InfoApi(val context: AppEntContext)  {

    @ApiAction(desc = "gets info about this build")
    fun build(): Build = context.app.build


    @ApiAction(desc = "get info about the application")
    fun about(): About = context.app.about


    @ApiAction(desc = "get info about the application")
    fun cmd(cmd: Request): About = context.app.about


    @ApiAction(desc = "gets info about the language")
    fun lang(): Lang = context.app.lang


    @ApiAction(desc = "gets info about the folders")
    fun dirs(): Folders = context.dirs ?: Folders.none


    @ApiAction(desc = "gets info about the start up time")
    fun start(): StartInfo = context.app.start


    @ApiAction(desc = "gets info about the status")
    fun status(): Status = context.app.status

}
