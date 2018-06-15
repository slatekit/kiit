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
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.apis.support.ApiBase
import slatekit.core.common.AppContext

/**
 * Created by kreddy on 3/23/2016.
 */
@Api(area = "infra", name = "encryption", desc = "api to encryption and decryption",
        auth = AuthModes.apiKey, roles = "admin", verb = Verbs.auto, protocol = Protocols.cli)
class EncryptApi(context: AppContext) : ApiBase(context){

    @ApiAction(desc = "encryptes the text")
    override fun encrypt(text: String): String {
        return context.enc?.encrypt(text) ?: text
    }


    @ApiAction(desc = "decrypts the text")
    override fun decrypt(text: String): String {
        return context.enc?.decrypt(text) ?: text
    }


    @ApiAction(desc = "encrypts all delimited tokens supplied")
    fun encryptKeyValue(key: String, value: String): String {
        return key + ": " + (context.enc?.encrypt(value) ?: value)
    }


    @ApiAction(desc = "encrypts all delimited tokens supplied")
    fun decryptKeyValue(key: String, value: String): String {
        return key + ": " + (context.enc?.decrypt(value) ?: value)
    }
}