package test.setup

import kiit.apis.Action
import kiit.apis.Api
import kiit.apis.AuthModes
import kiit.common.DateTime


/**
 * Sample 1:
 * This is the simplest example of APIs in Slate Kit.
 * APIs are designed to be "Universal" and "Protocol Independent" which means
 * that these can be hosted as Web/HTTP APIs and CLI ( Command Line )
 *
 * NOTES:
 * 1. POKO       : Plain old kotlin object without any framework code
 * 2. Actions    : Only public methods declared in this class will be exposed
 * 3. Protocol   : This API can be accessed via HTTP and/or on the CLI
 * 4. Arguments  : Method params are automatically loaded
 * 5. Annotations: This examples has 0 annotations, but you can add them
 *                 to explicitly declare and configure the APIs
 */

@Api(area = "tests", name = "SampleExtended", desc = "api to access and manage users 3", auth = AuthModes.NONE)
open class SampleExtendedApi : SamplePOKOApi() {
    @Action
    fun getSeconds():Int = DateTime.now().second

    @Action
    fun ping(greeting:String):String = "$greeting back"
}


open class SampleAnnoBaseApi {
    @Action()
    fun hello(greeting: String): String = "$greeting back"
}

open class SampleAnnoExtendedApi : SampleAnnoBaseApi() {
    @Action(name = "seconds")
    fun getSeconds():Int = DateTime.now().second

    @Action()
    fun ping(greeting:String):String = "$greeting back"
}
