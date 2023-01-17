/**
 *  <kiit_header>
 * url: www.kiit.dev
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

import kiit.apis.*
import kiit.common.*
import kiit.results.Outcome
import kiit.results.Success

@Api(area = "samples", name = "validation", desc = "api to access and manage users 3", auth = AuthModes.NONE)
class Sample_API_1_Validation() {


    @Action(desc = "activates a users account 3")
    fun processInputs(phone: String, code: Int, isOn: Boolean, date: DateTime): Outcome<String> {
        return Success("ok", msg = "inputs $phone, $code, $isOn, $date")
    }
}
