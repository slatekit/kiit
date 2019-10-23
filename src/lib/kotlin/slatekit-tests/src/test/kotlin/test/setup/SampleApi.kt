package test.setup

import slatekit.apis.*
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.apis.support.ApiBase
import slatekit.integration.common.AppEntContext


@Api(area = "app", name = "tests", desc = "sample to test features of Slate Kit APIs",
    auth = AuthModes.token, roles= "admin", verb = Verbs.auto, protocol = Protocols.all)
class SampleApi(context: AppEntContext): ApiBase(context) {

    @ApiAction(desc = "accepts supplied basic data types from send")
    fun defaultAnnotationValues(string1: String): String {
        return "$string1"
    }
}
