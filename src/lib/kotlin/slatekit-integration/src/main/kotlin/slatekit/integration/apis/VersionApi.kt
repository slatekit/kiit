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
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.apis.support.ApiWithSupport
import slatekit.common.info.Host
import slatekit.integration.common.AppEntContext


@Api(area = "app", name = "version", desc = "api to get version information",
        auth = AuthModes.apiKey, roles = "admin", verb = Verbs.post, protocol = Protocols.all)
class VersionApi(override val context: AppEntContext) : ApiWithSupport {

    @ApiAction(desc = "gets info about the host")
    fun host(): Host {
        return context.app.host
    }


    @ApiAction(desc = "get the version of the application")
    fun app(): String {
        return context.app.about.version
    }


    @ApiAction(desc = "gets the version of java")
    fun java(): String {
        return context.app.lang.version
    }


    @ApiAction(desc = "gets the version of scala")
    fun kotlin(): String {
        return context.app.lang.vendor
    }


    @ApiAction(desc = "gets the version of the system")
    fun version(): String {
        return context.app.host.version
    }
}