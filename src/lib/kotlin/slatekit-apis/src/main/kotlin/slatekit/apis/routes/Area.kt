package slatekit.apis.routes

/**
 * * ================================================================
 * Universal Route =  {AREA}.{API}.{ACTION}
 * Route           =  accounts.signup.register
 * Web             =  POST https://{host}/api/accounts/signup/register
 * CLI             =  :> accounts.signup.register -email=".." -pswd=".."
 * Queue           =  JSON { path: "account.signup.register", meta: { }, data : { } }
 * Class           =
 *      @Api(area = "samples", name = "core", ...)
 *      class Signup {
 *          @Action(desc = "processes an request with 0 parameters")
 *          suspend fun register(email:String, pswd:String): Outcome<UUID> {
 *              // code...
 *          }
 *      }
 * ================================================================
 * From the example above, this represents the area "accounts" and it's mapped list of apis/classes.
 *
 * The top most level qualifier in the Universal Routing Structure
 * Essentially the logical root of APIs
 * e.g.
 *
 * Format:  {area}.{api}.{action}
 * Tree  :
 *          { area_1 }
 *
 *              - { api_1 }
 *
 *                  - { action_a }
 *                  - { action_b }
 *
 *              - { api_2 }
 *
 *                  - { action_c }
 *                  - { action_d }
 *
 *              - { api_3 }
 *
 *                  - { action_d }
 *                  - { action_e }
 */
class Area(val name: String, val apis: Lookup<Api>)
