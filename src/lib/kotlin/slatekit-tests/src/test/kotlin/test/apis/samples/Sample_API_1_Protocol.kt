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

package test.apis.samples

import slatekit.apis.*
import slatekit.common.*
import slatekit.results.Outcome
import slatekit.results.Success

@Api(area = "samples", name = "core", desc = "api to access and manage users 3", auth = AuthModes.None, sources = [Sources.All])
class Sample_API_1_Protocol {


    @Action()
    fun processParent(name:String): Outcome<String> {
        return Success("ok", msg = "via parent:$name")
    }


    @Action( sources = [Sources.CLI])
    fun processCLI(name:String): Outcome<String> {
        return Success("ok", msg = "via cli:$name")
    }


    @Action( sources = [Sources.Web])
    fun processWeb(name:String): Outcome<String> {
        return Success("ok", msg = "via web:$name")
    }
}
