package slatekit.server.sample

import slatekit.apis.svcs.Authenticator
import slatekit.common.info.ApiKey
import slatekit.common.requests.Request
import slatekit.results.Notice
import slatekit.results.Success

class SampleAuth : Authenticator(listOf()) {

    /**
     * Override and implement this method if you want to completely handle authorization your self.
     * @param cmd         : The protocol independent API request. e.g. http://abc.com/api/myapp/users/activate
     * @param mode        : The auth-mode of the api ( refer to auth-mode for protocolo independent APIs )
     * @param actionRoles : The role setup on the API action
     * @param apiRoles    : The role setup on the API itself
     *
     * IMPORTANT:
     * 1. all you need to do to implement your authorization is to implement the getUserRoles below
     *
     * NOTES:
     * 1. see base class implementation for details.
     * 2. the auth modes on the apis can be "app-roles" or "key-roles" ( api-keys )
     * 3. the base class properly delegates handling the auth modes.
     * @return
     */
    override fun isAuthorized(cmd: Request, mode: String, actionRoles: String, apiRoles: String) : Notice<Boolean> {
        return Success(true)
    }


    companion object {
        /**
         * Example of using API keys for protected access to APIs    *
         * NOTE: API keys may be sufficient for local, internal network based access to APIs.
         * You should use some token/OAuth based approach in other situations.
         *
         * NOTE: These should be kept secret in some way, but shown here for sample purposes.
         *
         * @return
         */
        val apiKeys = listOf(
                    ApiKey("user" , "7BF84B28FC8A41BBA3FDFA48D2B462DA", "user"                    ),
                    ApiKey("po"   , "0F66CD55079C42FF85C001846472343C", "user,po"                 ),
                    ApiKey("qa"   , "EB7EB37764AD4411A1763E6A593992BD", "user,po,qa"              ),
                    ApiKey("dev"  , "3E35584A8DE0460BB28D6E0D32FB4CFD", "user,po,qa,dev"          ),
                    ApiKey("ops"  , "5020F4A237A443B4BEDC37D8A08588A3", "user,po,qa,dev,ops"      ),
                    ApiKey("admin", "54B1817194C1450B886404C6BEA81673", "user,po,qa,dev,ops,admin")
            )
    }
}