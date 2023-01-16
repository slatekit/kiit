/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * 
 * 
  *  </kiit_header>
 */

package test.apis.samples

import slatekit.apis.*
import kiit.common.*
import kiit.results.Outcome
import kiit.results.Success

@Api(area = "samples", name = "core", desc = "api to access and manage users 3", auth = AuthModes.NONE, sources = [Sources.ALL])
class Sample_API_1_Protocol {


    @Action()
    fun processParent(name:String): Outcome<String> {
        return Success("ok", msg = "via parent:$name")
    }


    @Action( sources = [Sources.CLI])
    fun processCLI(name:String): Outcome<String> {
        return Success("ok", msg = "via cli:$name")
    }


    @Action( sources = [Sources.WEB])
    fun processWeb(name:String): Outcome<String> {
        return Success("ok", msg = "via web:$name")
    }
}
