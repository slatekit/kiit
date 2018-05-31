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

package slatekit.integration.apis


import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.ApiConstants
import slatekit.apis.support.ApiBase
import slatekit.core.common.AppContext

/**
 * Created by kreddy on 3/23/2016.
 */
@Api(area = "infra", name = "encryption", desc = "api to encryption and decryption",
        roles = "?", auth = "app", verb = "*", protocol = ApiConstants.SourceCLI)
class EncryptApi(context: AppContext) : ApiBase(context){

    @ApiAction(name = "", desc = "encryptes the text", roles = "")
    override fun encrypt(text: String): String {
        return context.enc?.encrypt(text) ?: text
    }


    @ApiAction(name = "", desc = "decrypts the text", roles = "")
    override fun decrypt(text: String): String {
        return context.enc?.decrypt(text) ?: text
    }


    @ApiAction(name = "", desc = "encrypts all delimited tokens supplied", roles = "")
    fun encryptKeyValue(key: String, value: String): String {
        return key + ": " + (context.enc?.encrypt(value) ?: value)
    }


    @ApiAction(name = "", desc = "encrypts all delimited tokens supplied", roles = "")
    fun decryptKeyValue(key: String, value: String): String {
        return key + ": " + (context.enc?.decrypt(value) ?: value)
    }
}