package test.setup

import slatekit.apis.*
import slatekit.apis.setup.AuthModes
import slatekit.apis.setup.Protocols
import slatekit.apis.setup.Verbs
import slatekit.apis.support.ApiBase
import slatekit.integration.common.AppEntContext


@Api(area = "app", name = "tests", desc = "sample to test features of Slate Kit APIs",
    auth = AuthModes.token, roles= "admin", verb = Verbs.Auto, protocol = Protocols.All)
class SampleApi(context: AppEntContext): ApiBase(context) {

    @Action(desc = "accepts supplied basic data types from send")
    fun defaultAnnotationValues(string1: String): String {
        return "$string1"
    }
}
