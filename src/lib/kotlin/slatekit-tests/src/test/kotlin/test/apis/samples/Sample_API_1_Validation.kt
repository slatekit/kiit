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
import slatekit.common.types.Doc
import slatekit.common.requests.Request
import slatekit.common.validations.ValidationFuncs
import slatekit.integration.common.AppEntContext
import slatekit.results.Outcome
import slatekit.results.Success
import slatekit.results.builders.Outcomes

@Api(area = "samples", name = "validation", desc = "api to access and manage users 3", auth = AuthModes.NONE)
class Sample_API_1_Validation() {


    @Action(desc = "activates a users account 3")
    fun processInputs(phone: String, code: Int, isOn: Boolean, date: DateTime): Outcome<String> {
        return Success("ok", msg = "inputs $phone, $code, $isOn, $date")
    }
}
