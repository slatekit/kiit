package slatekit.samples.common.auth

import slatekit.apis.support.Authenticator
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
                    ApiKey("user" , "A2312345678901234567890123456789", "user"                    ),
                    ApiKey("po"   , "B2312345678901234567890123456789", "user,po"                 ),
                    ApiKey("qa"   , "C2312345678901234567890123456789", "user,po,qa"              ),
                    ApiKey("dev"  , "D2312345678901234567890123456789", "user,po,qa,dev"          ),
                    ApiKey("ops"  , "E2312345678901234567890123456789", "user,po,qa,dev,ops"      ),
                    ApiKey("admin", "F2312345678901234567890123456789", "user,po,qa,dev,ops,admin")
            )
    }
}