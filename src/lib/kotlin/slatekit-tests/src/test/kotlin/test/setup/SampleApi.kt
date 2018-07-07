package test.setup

import slatekit.apis.*
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.apis.support.ApiBase
import slatekit.common.*
import slatekit.common.encrypt.EncDouble
import slatekit.common.encrypt.EncInt
import slatekit.common.encrypt.EncLong
import slatekit.common.encrypt.EncString
import slatekit.common.results.ResultFuncs.success
import slatekit.common.types.Email
import slatekit.common.types.PhoneUS
import slatekit.integration.common.AppEntContext


@Api(area = "app", name = "tests", desc = "sample to test features of Slate Kit APIs",
    auth = AuthModes.token, roles= "admin", verb = Verbs.auto, protocol = Protocols.all)
class SampleApi(context: AppEntContext): ApiBase(context) {

    @ApiAction(desc = "accepts supplied basic data types from request")
    fun defaultAnnotationValues(string1: String): String {
        return "$string1"
    }
}
