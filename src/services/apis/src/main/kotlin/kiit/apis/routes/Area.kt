package kiit.apis.routes

import kiit.apis.ApiConstants

/**
 * * ================================================================
 * Universal Route =  {AREA}.{API}.{ACTION}
 * Route           =  accounts.signup.register
 * Web             =  POST https://{host}/api/accounts/signup/register
 * CLI             =  :> accounts.signup.register -email=".." -pswd=".."
 * Queue           =  JSON { path: "account.signup.register", meta: { }, data : { } }
 * Class           =
 *      @Api(area = "accounts", name = "signup", ...)
 *      class Signup {
 *          @Action(desc = "processes an request with 0 parameters")
 *          suspend fun register(email:String, pswd:String): Outcome<UUID> {
 *              // code...
 *          }
 *      }
 * ================================================================
 * From the example above, this represents the area "accounts" and it's mapped list of apis/classes
 */
class Area(val name: String, val version:String = ApiConstants.versionZero) {
    val fullname:String = "$version.$name"
}
