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
import slatekit.common.content.Doc
import slatekit.common.requests.Request
import slatekit.common.validations.ValidationFuncs
import slatekit.integration.common.AppEntContext
import slatekit.results.Outcome
import slatekit.results.Success
import slatekit.results.builders.Outcomes

@Api(area = "samples", name = "core", desc = "api to access and manage users 3", auth = AuthModes.None, protocols = [Sources.All])
class Sample_API_1_Protocol {


    @Action()
    fun processParent(name:String): Outcome<String> {
        return Success("ok", msg = "via parent:$name")
    }


    @Action( protocols = [Sources.CLI])
    fun processCLI(name:String): Outcome<String> {
        return Success("ok", msg = "via cli:$name")
    }


    @Action( protocols = [Sources.Web])
    fun processWeb(name:String): Outcome<String> {
        return Success("ok", msg = "via web:$name")
    }
}
