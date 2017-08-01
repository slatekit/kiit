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
import slatekit.apis.support.ApiWithSupport
import slatekit.integration.common.AppEntContext


@Api(area = "sys", name = "version", desc = "api to get version information", roles = "ops", auth = "key-roles", verb = "*", protocol = "*")
class VersionApi(override val context: AppEntContext) : ApiWithSupport {

    @ApiAction(desc = "get the version of the application", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun app(): String {
        return context.app.about.version
    }


    @ApiAction(desc = "gets the version of java", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun java(): String {
        return context.app.lang.version
    }


    @ApiAction(desc = "gets the version of scala", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun kotlin(): String {
        return context.app.lang.vendor
    }


    @ApiAction(desc = "gets the version of the system", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun host(): String {
        return context.app.host.version
    }
}